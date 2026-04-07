package com.pwms.report.service;

import com.pwms.report.client.*;
import com.pwms.report.dto.*;
import com.pwms.report.exception.ReportNotFoundException;
import com.pwms.report.interfaces.ReportIntf;
import com.pwms.report.model.Report;
import com.pwms.report.model.Report.ReportStatus;
import com.pwms.report.repository.ReportRepository;
import com.pwms.report.util.PdfReportGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService implements ReportIntf {

    private final ReportRepository   reportRepo;
    private final PatientClient      patientClient;       // HTTP
    private final WellnessPlanClient planClient;          // HTTP
    private final ProgressClient     progressClient;      // HTTP
    private final NotificationClient notificationClient;  // HTTP
    private final PdfReportGenerator pdfGenerator;

    // ── Step 1: Admin previews pre-filled data ────────────────
    @Override
    public ReportPreviewDTO getReportPreview(int patientId, int planId)
            throws ReportNotFoundException {
        try {
            PatientDTO        patient = patientClient.getPatientById(patientId);
            PlanDTO           plan    = planClient.getPlanById(planId);
            ProgressSummaryDTO summary = progressClient.getDailySummary(patientId, planId);

            LocalDate from = LocalDate.now().minusDays(6);
            LocalDate to   = LocalDate.now();

            return ReportPreviewDTO.builder()
                    .patientId(patientId)
                    .patientName(patient.getPatientName())
                    .planId(planId)
                    .planName(plan.getPlanName())
                    .totalActivities(summary.getTotalActivities())
                    .completedActivities(summary.getCompletedActivities())
                    .completionPercentage(summary.getCompletionPercentage())
                    .dateRange(from + " to " + to)
                    .build();
        } catch (Exception e) {
            throw new ReportNotFoundException(
                    "Could not load preview for patientId: " + patientId +
                            " — " + e.getMessage());
        }
    }

    // ── Step 2: Admin generates + publishes report ────────────
    @Override
    public ReportDTO generateReport(int patientId, int planId,
                                    ReportRequestDTO request)
            throws ReportNotFoundException {
        try {
            PatientDTO         patient = patientClient.getPatientById(patientId);
            PlanDTO            plan    = planClient.getPlanById(planId);
            ProgressSummaryDTO summary = progressClient.getDailySummary(patientId, planId);

            String systemSummary = String.format(
                    "Patient: %s | Plan: %s | Date: %s | Completed: %d/%d (%.2f%%)",
                    patient.getPatientName(),
                    plan.getPlanName(),
                    LocalDate.now(),
                    summary.getCompletedActivities(),
                    summary.getTotalActivities(),
                    summary.getCompletionPercentage()
            );

            Report saved = reportRepo.save(Report.builder()
                    .patientId(patientId)
                    .planId(planId)
                    .generatedBy(request.getAdminId())
                    .summary(systemSummary)
                    .adminSummary(request.getAdminSummary())
                    .status(ReportStatus.PUBLISHED)
                    .build());

            // Notify patient via HTTP
            notificationClient.notifyReportShared(patientId, planId);

            // Notify appointment if plan no longer active
            boolean planCompleted = planClient
                    .getActiveAssignmentsByPatient(patientId)
                    .stream()
                    .noneMatch(a -> a.getPlan().getPlanId() == planId
                            && "ACTIVE".equals(a.getStatus()));

            if (planCompleted) {
                notificationClient.notifyAppointmentReminder(patientId, planId);
            }

            return toDTO(saved, patient.getPatientName(),
                    plan.getPlanName(), summary);

        } catch (Exception e) {
            throw new ReportNotFoundException(
                    "Failed to generate report: " + e.getMessage());
        }
    }

    // ── Admin — all reports ───────────────────────────────────
    @Override
    public List<ReportDTO> getAllReportsByPatient(int patientId)
            throws ReportNotFoundException {
        List<Report> reports = reportRepo.findByPatientId(patientId);
        if (reports.isEmpty()) throw new ReportNotFoundException(
                "No reports found for patientId: " + patientId);
        return enrichList(reports);
    }

    // ── Patient — published reports only ─────────────────────
    @Override
    public List<ReportDTO> getPublishedReportsByPatient(int patientId)
            throws ReportNotFoundException {
        List<Report> reports = reportRepo.findByPatientIdAndStatus(
                patientId, ReportStatus.PUBLISHED);
        if (reports.isEmpty()) throw new ReportNotFoundException(
                "No published reports found for patientId: " + patientId);
        return enrichList(reports);
    }

    // ── Single report ─────────────────────────────────────────
    @Override
    public ReportDTO getReportById(int reportId) throws ReportNotFoundException {
        Report r = reportRepo.findById(reportId)
                .orElseThrow(() -> new ReportNotFoundException(
                        "Report not found with id: " + reportId));
        return enrichSingle(r);
    }

    // ── Date range analytics ──────────────────────────────────
    @Override
    public List<ReportDTO> getReportsByDateRange(
            int patientId, LocalDate from, LocalDate to)
            throws ReportNotFoundException {
        List<Report> reports = reportRepo
                .findByPatientIdAndDateBetweenOrderByDateDesc(patientId, from, to);
        if (reports.isEmpty()) throw new ReportNotFoundException(
                "No reports found between " + from + " and " + to);
        return enrichList(reports);
    }

    // ── PDF download ──────────────────────────────────────────
    @Override
    public byte[] downloadReportPdf(int reportId) throws ReportNotFoundException {
        return pdfGenerator.generate(getReportById(reportId));
    }

    // ── Helpers ───────────────────────────────────────────────

    private List<ReportDTO> enrichList(List<Report> reports) {
        return reports.stream()
                .map(this::enrichSingle)
                .collect(Collectors.toList());
    }

    private ReportDTO enrichSingle(Report r) {
        String patientName = "Unknown";
        String planName    = "Unknown";
        int total = 0, completed = 0;
        double pct = 0.0;
        try {
            patientName = patientClient.getPatientById(r.getPatientId()).getPatientName();
            planName    = planClient.getPlanById(r.getPlanId()).getPlanName();
            ProgressSummaryDTO s = progressClient.getDailySummary(
                    r.getPatientId(), r.getPlanId());
            total     = s.getTotalActivities();
            completed = s.getCompletedActivities();
            pct       = s.getCompletionPercentage();
        } catch (Exception ignored) {}

        return toDTO(r, patientName, planName, total, completed, pct);
    }

    private ReportDTO toDTO(Report r, String patientName, String planName,
                            ProgressSummaryDTO s) {
        return toDTO(r, patientName, planName,
                s.getTotalActivities(), s.getCompletedActivities(),
                s.getCompletionPercentage());
    }

    private ReportDTO toDTO(Report r, String patientName, String planName,
                            int total, int completed, double pct) {
        return ReportDTO.builder()
                .reportId(r.getReportId())
                .patientId(r.getPatientId())
                .patientName(patientName)
                .planId(r.getPlanId())
                .planName(planName)
                .generatedBy(r.getGeneratedBy())
                .summary(r.getSummary())
                .adminSummary(r.getAdminSummary())
                .date(r.getDate())
                .status(r.getStatus().name())
                .totalActivities(total)
                .completedActivities(completed)
                .completionPercentage(pct)
                .build();
    }
}