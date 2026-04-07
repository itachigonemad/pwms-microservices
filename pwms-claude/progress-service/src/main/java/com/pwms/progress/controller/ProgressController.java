package com.pwms.progress.controller;

import com.pwms.progress.dto.*;
import com.pwms.progress.exception.*;
import com.pwms.progress.interfaces.ProgressIntf;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressIntf progressService;

    // POST /api/progress/init?patientId=1&planId=2
    // Called by admin when assigning a plan — seeds PENDING rows
    @PostMapping("/init")
    public ResponseEntity<String> initializeProgress(
            @RequestParam int patientId,
            @RequestParam int planId) throws ProgressAlreadyExistsException {
        progressService.initializeProgress(patientId, planId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Progress initialized for patientId: " + patientId +
                        " planId: " + planId);
    }

    // PATCH /api/progress/update/{patientId}
    // Patient marks activity as DONE / SKIPPED
    @PatchMapping("/update/{patientId}")
    public ResponseEntity<ProgressResponseDTO> updateStatus(
            @PathVariable int patientId,
            @RequestBody StatusUpdateDTO dto) throws ProgressNotFoundException {
        return ResponseEntity.ok(progressService.updateStatus(patientId, dto));
    }

    // GET /api/progress/patient/{patientId}
    // All progress rows for a patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ProgressResponseDTO>> getByPatient(
            @PathVariable int patientId) throws ProgressNotFoundException {
        return ResponseEntity.ok(progressService.getProgressByPatient(patientId));
    }

    // GET /api/progress/patient/{patientId}/plan/{planId}
    // Progress for a specific patient + plan
    @GetMapping("/patient/{patientId}/plan/{planId}")
    public ResponseEntity<List<ProgressResponseDTO>> getByPatientAndPlan(
            @PathVariable int patientId,
            @PathVariable int planId) throws ProgressNotFoundException {
        return ResponseEntity.ok(
                progressService.getProgressByPatientAndPlan(patientId, planId));
    }

    // GET /api/progress/summary/{patientId}/plan/{planId}
    // Daily completion summary — used by report module + dashboard
    @GetMapping("/summary/{patientId}/plan/{planId}")
    public ResponseEntity<ProgressSummaryDTO> getDailySummary(
            @PathVariable int patientId,
            @PathVariable int planId) throws ProgressNotFoundException {
        return ResponseEntity.ok(progressService.getDailySummary(patientId, planId));
    }
}