package com.pwms.progress.service;

import com.pwms.progress.client.NotificationClient;
import com.pwms.progress.client.WellnessPlanClient;
import com.pwms.progress.dto.*;
import com.pwms.progress.exception.*;
import com.pwms.progress.interfaces.ProgressIntf;
import com.pwms.progress.model.Progress;
import com.pwms.progress.model.Progress.ActivityStatus;
import com.pwms.progress.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService implements ProgressIntf {

    private final ProgressRepository progressRepo;
    private final WellnessPlanClient  planClient;         // HTTP client
    private final NotificationClient  notificationClient; // HTTP client

    private static final int DEFAULT_ADMIN_ID = 1;

    // ── Initialize progress rows when plan is assigned ────────
    @Override
    public void initializeProgress(int patientId, int planId)
            throws ProgressAlreadyExistsException {

        List<Progress> existing = progressRepo.findByPatientIdAndPlanId(patientId, planId);
        if (!existing.isEmpty()) {
            throw new ProgressAlreadyExistsException(
                    "Progress already initialized for patientId: " + patientId +
                            " planId: " + planId);
        }

        // HTTP call to wellnessplan-service
        List<ActivityDTO> activities = planClient.getActivitiesByPlanId(planId);

        List<Progress> seedRows = activities.stream()
                .map(a -> Progress.builder()
                        .patientId(patientId)
                        .planId(planId)
                        .activityId(a.getActivityId())
                        .status(ActivityStatus.PENDING)
                        .trackedDate(LocalDate.now())
                        .build())
                .collect(Collectors.toList());

        progressRepo.saveAll(seedRows);
    }

    // ── Patient updates activity status ───────────────────────
    @Override
    public ProgressResponseDTO updateStatus(int patientId, StatusUpdateDTO dto)
            throws ProgressNotFoundException {

        Progress p = progressRepo
                .findByPatientIdAndActivityIdAndTrackedDate(
                        patientId, dto.getActivityId(), LocalDate.now())
                .orElseThrow(() -> new ProgressNotFoundException(
                        "No progress row found for activityId: " + dto.getActivityId() +
                                " on " + LocalDate.now()));

        p.setStatus(dto.getStatus());
        Progress saved = progressRepo.save(p);

        String activityName = getActivityName(saved.getPlanId(), saved.getActivityId());

        // ── Trigger notifications via HTTP ────────────────────
        if (dto.getStatus() == ActivityStatus.DONE) {

            notificationClient.notifyActivityAppreciation(
                    patientId, saved.getPlanId(), activityName);

            if (isAllActivitiesDone(patientId, saved.getPlanId())) {
                notificationClient.notifyPlanCompleted(
                        patientId, saved.getPlanId(), DEFAULT_ADMIN_ID);
                notificationClient.notifyAppointmentReminder(
                        patientId, saved.getPlanId());
                ProgressSummaryDTO summary = getDailySummary(patientId, saved.getPlanId());
                notificationClient.notifyWeeklySummary(
                        patientId, saved.getPlanId(), summary.getCompletionPercentage());
            }

        } else if (dto.getStatus() == ActivityStatus.PENDING) {
            notificationClient.notifyActivityReminder(
                    patientId, saved.getPlanId(), activityName);
        }

        return toDTO(saved, activityName);
    }

    // ── Get full progress for a patient ───────────────────────
    @Override
    public List<ProgressResponseDTO> getProgressByPatient(int patientId)
            throws ProgressNotFoundException {
        List<Progress> records = progressRepo.findByPatientId(patientId);
        if (records.isEmpty()) throw new ProgressNotFoundException(
                "No progress found for patientId: " + patientId);
        return enrichWithActivityNames(records);
    }

    // ── Get progress for patient + plan ───────────────────────
    @Override
    public List<ProgressResponseDTO> getProgressByPatientAndPlan(
            int patientId, int planId) throws ProgressNotFoundException {
        List<Progress> records = progressRepo.findByPatientIdAndPlanId(patientId, planId);
        if (records.isEmpty()) throw new ProgressNotFoundException(
                "No progress found for patientId: " + patientId + " planId: " + planId);
        return enrichWithActivityNames(records);
    }

    // ── Daily summary ─────────────────────────────────────────
    @Override
    public ProgressSummaryDTO getDailySummary(int patientId, int planId)
            throws ProgressNotFoundException {
        List<ActivityDTO> activities = planClient.getActivitiesByPlanId(planId);
        int total = activities.size();
        long completed = progressRepo.countByStatusForDate(
                patientId, planId, ActivityStatus.DONE, LocalDate.now());
        double pct = total > 0
                ? Math.round((completed * 100.0 / total) * 100.0) / 100.0 : 0.0;

        return ProgressSummaryDTO.builder()
                .patientId(patientId)
                .planId(planId)
                .date(LocalDate.now())
                .totalActivities(total)
                .completedActivities((int) completed)
                .completionPercentage(pct)
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────

    private boolean isAllActivitiesDone(int patientId, int planId) {
        List<Progress> todayRecords = progressRepo
                .findByPatientIdAndPlanIdAndTrackedDateBetween(
                        patientId, planId, LocalDate.now(), LocalDate.now());
        return !todayRecords.isEmpty() &&
                todayRecords.stream()
                        .allMatch(p -> p.getStatus() == ActivityStatus.DONE);
    }

    private List<ProgressResponseDTO> enrichWithActivityNames(List<Progress> records) {
        if (records.isEmpty()) return List.of();
        int planId = records.get(0).getPlanId();
        Map<Integer, String> nameMap;
        try {
            nameMap = planClient.getActivitiesByPlanId(planId)
                    .stream()
                    .collect(Collectors.toMap(ActivityDTO::getActivityId,
                            ActivityDTO::getActivityName));
        } catch (Exception e) {
            nameMap = Map.of();
        }
        final Map<Integer, String> finalMap = nameMap;
        return records.stream()
                .map(p -> toDTO(p, finalMap.getOrDefault(p.getActivityId(), "Unknown")))
                .collect(Collectors.toList());
    }

    private String getActivityName(int planId, int activityId) {
        try {
            return planClient.getActivitiesByPlanId(planId).stream()
                    .filter(a -> a.getActivityId() == activityId)
                    .map(ActivityDTO::getActivityName)
                    .findFirst().orElse("Unknown");
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private ProgressResponseDTO toDTO(Progress p, String activityName) {
        return ProgressResponseDTO.builder()
                .progressId(p.getProgressId())
                .patientId(p.getPatientId())
                .planId(p.getPlanId())
                .activityId(p.getActivityId())
                .activityName(activityName)
                .status(p.getStatus().name())
                .trackedDate(p.getTrackedDate())
                .build();
    }
}