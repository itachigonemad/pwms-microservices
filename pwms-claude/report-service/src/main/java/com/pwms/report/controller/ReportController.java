package com.pwms.report.controller;

import com.pwms.report.dto.*;
import com.pwms.report.exception.ReportNotFoundException;
import com.pwms.report.interfaces.ReportIntf;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportIntf reportService;

    // GET /api/reports/preview/{patientId}/{planId}
    // Step 1 — Admin fetches pre-filled data before writing summary
    @GetMapping("/preview/{patientId}/{planId}")
    public ResponseEntity<ReportPreviewDTO> getPreview(
            @PathVariable int patientId,
            @PathVariable int planId) throws ReportNotFoundException {
        return ResponseEntity.ok(
                reportService.getReportPreview(patientId, planId));
    }

    // POST /api/reports/generate/{patientId}/{planId}
    // Step 2 — Admin submits report with their summary
    @PostMapping("/generate/{patientId}/{planId}")
    public ResponseEntity<ReportDTO> generateReport(
            @PathVariable int patientId,
            @PathVariable int planId,
            @RequestBody ReportRequestDTO request) throws ReportNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.generateReport(patientId, planId, request));
    }

    // GET /api/reports/admin/patient/{patientId}
    // Admin — all reports for a patient (all statuses)
    @GetMapping("/admin/patient/{patientId}")
    public ResponseEntity<List<ReportDTO>> getAllReports(
            @PathVariable int patientId) throws ReportNotFoundException {
        return ResponseEntity.ok(
                reportService.getAllReportsByPatient(patientId));
    }

    // GET /api/reports/patient/{patientId}
    // Patient — published reports only
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ReportDTO>> getPublishedReports(
            @PathVariable int patientId) throws ReportNotFoundException {
        return ResponseEntity.ok(
                reportService.getPublishedReportsByPatient(patientId));
    }

    // GET /api/reports/{reportId}
    // Single report — both admin and patient
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDTO> getById(
            @PathVariable int reportId) throws ReportNotFoundException {
        return ResponseEntity.ok(reportService.getReportById(reportId));
    }

    // GET /api/reports/patient/{patientId}/range?from=2026-03-01&to=2026-03-31
    // Date range analytics
    @GetMapping("/patient/{patientId}/range")
    public ResponseEntity<List<ReportDTO>> getByDateRange(
            @PathVariable int patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to)
            throws ReportNotFoundException {
        return ResponseEntity.ok(
                reportService.getReportsByDateRange(patientId, from, to));
    }

    // GET /api/reports/download/{reportId}
    // PDF download — uses saved report, no regeneration
    @GetMapping("/download/{reportId}")
    public ResponseEntity<byte[]> downloadPdf(
            @PathVariable int reportId) throws ReportNotFoundException {
        byte[] pdf = reportService.downloadReportPdf(reportId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=report_" + reportId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}