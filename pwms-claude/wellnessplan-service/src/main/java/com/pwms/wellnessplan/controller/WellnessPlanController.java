package com.pwms.wellnessplan.controller;

import com.pwms.wellnessplan.dto.*;
import com.pwms.wellnessplan.exception.*;
import com.pwms.wellnessplan.interfaces.WellnessPlanIntf;
import com.pwms.wellnessplan.model.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class WellnessPlanController {

    private final WellnessPlanIntf planService;

    // POST /api/plans
    // Body: { "planName": "Weight Loss", "activityNames": ["Walking","Yoga","Diet"] }
    @PostMapping
    public ResponseEntity<WellnessPlanDTO> createPlan(
            @RequestBody WellnessPlanRequestDTO request)
            throws PlanAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planService.createPlan(request));
    }

    // GET /api/plans
    @GetMapping
    public ResponseEntity<List<WellnessPlanDTO>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }

    // GET /api/plans/{id}
    @GetMapping("/{id}")
    public ResponseEntity<WellnessPlanDTO> getPlanById(
            @PathVariable int id) throws PlanNotFoundException {
        return ResponseEntity.ok(planService.getPlanById(id));
    }

    // PUT /api/plans/{id}
    @PutMapping("/{id}")
    public ResponseEntity<WellnessPlanDTO> updatePlan(
            @PathVariable int id,
            @RequestBody WellnessPlanRequestDTO request)
            throws PlanNotFoundException {
        return ResponseEntity.ok(planService.updatePlan(id, request));
    }

    // DELETE /api/plans/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlan(
            @PathVariable int id) throws PlanNotFoundException {
        planService.deletePlan(id);
        return ResponseEntity.ok("Plan with id " + id + " deleted successfully.");
    }

    // GET /api/plans/{id}/activities
    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityDTO>> getActivities(
            @PathVariable int id) throws PlanNotFoundException {
        return ResponseEntity.ok(
                planService.getActivitiesByPlanId(id).stream()
                        .map(a -> new ActivityDTO(a.getActivityId(), a.getActivityName()))
                        .collect(Collectors.toList()));
    }

    // POST /api/plans/assign?patientId=1&planId=1
    @PostMapping("/assign")
    public ResponseEntity<PlanAssignmentDTO> assignPlan(
            @RequestParam int patientId,
            @RequestParam int planId)
            throws PlanNotFoundException, PatientAlreadyAssignedException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(planService.assignPlanToPatient(patientId, planId));
    }

    // GET /api/plans/assignments/patient/{patientId}
    @GetMapping("/assignments/patient/{patientId}")
    public ResponseEntity<List<PlanAssignmentDTO>> getByPatient(
            @PathVariable int patientId) {
        return ResponseEntity.ok(
                planService.getActiveAssignmentsByPatient(patientId));
    }

    // GET /api/plans/assignments/plan/{planId}
    @GetMapping("/assignments/plan/{planId}")
    public ResponseEntity<List<PlanAssignmentDTO>> getByPlan(
            @PathVariable int planId) {
        return ResponseEntity.ok(
                planService.getActiveAssignmentsByPlan(planId));
    }
}