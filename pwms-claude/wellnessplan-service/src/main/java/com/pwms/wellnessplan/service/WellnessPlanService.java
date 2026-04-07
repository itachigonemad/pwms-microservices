package com.pwms.wellnessplan.service;

import com.pwms.wellnessplan.dto.*;
import com.pwms.wellnessplan.exception.*;
import com.pwms.wellnessplan.interfaces.WellnessPlanIntf;
import com.pwms.wellnessplan.model.*;
import com.pwms.wellnessplan.model.PlanAssignment.AssignmentStatus;
import com.pwms.wellnessplan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WellnessPlanService implements WellnessPlanIntf {

    private final WellnessPlanRepository   planRepo;
    private final ActivityRepository       activityRepo;
    private final PlanAssignmentRepository assignmentRepo;

    // ── Plan operations ───────────────────────────────────────

    @Override
    @Transactional
    public WellnessPlanDTO createPlan(WellnessPlanRequestDTO request)
            throws PlanAlreadyExistsException {

        if (planRepo.existsByPlanName(request.getPlanName())) {
            throw new PlanAlreadyExistsException(
                    "Plan already exists with name: " + request.getPlanName());
        }

        WellnessPlan plan = new WellnessPlan();
        plan.setPlanName(request.getPlanName());

        if (request.getActivityNames() != null) {
            List<Activity> activities = request.getActivityNames().stream()
                    .map(name -> {
                        Activity a = new Activity();
                        a.setActivityName(name);
                        a.setPlan(plan);
                        return a;
                    })
                    .collect(Collectors.toList());
            plan.setActivities(activities);
        }

        WellnessPlan saved = planRepo.save(plan);
        return toPlanDTO(saved);
    }

    @Override
    public WellnessPlanDTO getPlanById(int planId) throws PlanNotFoundException {
        WellnessPlan plan = findPlanById(planId);
        return toPlanDTO(plan);
    }

    @Override
    public List<WellnessPlanDTO> getAllPlans() {
        return planRepo.findAll().stream()
                .map(this::toPlanDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WellnessPlanDTO updatePlan(int planId, WellnessPlanRequestDTO request)
            throws PlanNotFoundException {

        WellnessPlan existing = findPlanById(planId);
        existing.setPlanName(request.getPlanName());
        existing.getActivities().clear();

        if (request.getActivityNames() != null) {
            request.getActivityNames().forEach(name -> {
                Activity a = new Activity();
                a.setActivityName(name);
                a.setPlan(existing);
                existing.getActivities().add(a);
            });
        }

        return toPlanDTO(planRepo.save(existing));
    }

    @Override
    public void deletePlan(int planId) throws PlanNotFoundException {
        if (!planRepo.existsById(planId)) {
            throw new PlanNotFoundException("Plan not found with id: " + planId);
        }
        planRepo.deleteById(planId);
    }

    // ── Activity operations ───────────────────────────────────

    @Override
    public List<Activity> getActivitiesByPlanId(int planId) throws PlanNotFoundException {
        findPlanById(planId);
        List<Activity> activities = activityRepo.findByPlan_PlanId(planId);
        if (activities.isEmpty()) {
            throw new PlanNotFoundException(
                    "No activities found for planId: " + planId);
        }
        return activities;
    }

    // ── Assignment operations ─────────────────────────────────

    @Override
    public PlanAssignmentDTO assignPlanToPatient(int patientId, int planId)
            throws PlanNotFoundException, PatientAlreadyAssignedException {

        WellnessPlan plan = findPlanById(planId);

        assignmentRepo.findByPatientIdAndPlan_PlanId(patientId, planId)
                .ifPresent(a -> {
                    throw new RuntimeException(
                            "Patient " + patientId + " is already assigned to plan " + planId);
                });

        PlanAssignment assignment = new PlanAssignment();
        assignment.setPatientId(patientId);
        assignment.setPlan(plan);
        assignment.setAssignedDate(LocalDate.now());
        assignment.setStatus(AssignmentStatus.ACTIVE);

        return toAssignmentDTO(assignmentRepo.save(assignment));
    }

    @Override
    public List<PlanAssignmentDTO> getActiveAssignmentsByPatient(int patientId) {
        return assignmentRepo
                .findByPatientIdAndStatus(patientId, AssignmentStatus.ACTIVE)
                .stream()
                .map(this::toAssignmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PlanAssignmentDTO> getActiveAssignmentsByPlan(int planId) {
        return assignmentRepo
                .findByPlan_PlanIdAndStatus(planId, AssignmentStatus.ACTIVE)
                .stream()
                .map(this::toAssignmentDTO)
                .collect(Collectors.toList());
    }

    // ── Internal helper — raw entity fetch ───────────────────
    private WellnessPlan findPlanById(int planId) throws PlanNotFoundException {
        return planRepo.findById(planId)
                .orElseThrow(() -> new PlanNotFoundException(
                        "Plan not found with id: " + planId));
    }

    // ── Mappers ───────────────────────────────────────────────

    // Loads activities from DB directly — avoids lazy load on entity
    private WellnessPlanDTO toPlanDTO(WellnessPlan plan) {
        List<ActivityDTO> activities = activityRepo
                .findByPlan_PlanId(plan.getPlanId())
                .stream()
                .map(a -> new ActivityDTO(a.getActivityId(), a.getActivityName()))
                .collect(Collectors.toList());

        return WellnessPlanDTO.builder()
                .planId(plan.getPlanId())
                .planName(plan.getPlanName())
                .activities(activities)
                .build();
    }

    private PlanAssignmentDTO toAssignmentDTO(PlanAssignment a) {
        return PlanAssignmentDTO.builder()
                .assignmentId(a.getAssignmentId())
                .patientId(a.getPatientId())
                .planId(a.getPlan().getPlanId())
                .planName(a.getPlan().getPlanName())
                .assignedDate(a.getAssignedDate())
                .status(a.getStatus().name())
                .build();
    }
}