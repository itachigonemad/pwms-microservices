package com.pwms.wellnessplan.interfaces;

import com.pwms.wellnessplan.dto.*;
import com.pwms.wellnessplan.exception.*;
import com.pwms.wellnessplan.model.*;

import java.util.List;

public interface WellnessPlanIntf {

    // Returns DTO — no lazy load issue
    WellnessPlanDTO createPlan(WellnessPlanRequestDTO request)
            throws PlanAlreadyExistsException;

    WellnessPlanDTO getPlanById(int planId) throws PlanNotFoundException;

    List<WellnessPlanDTO> getAllPlans();

    WellnessPlanDTO updatePlan(int planId, WellnessPlanRequestDTO request)
            throws PlanNotFoundException;

    void deletePlan(int planId) throws PlanNotFoundException;

    // Returns raw Activity — only used internally and by /activities endpoint
    List<Activity> getActivitiesByPlanId(int planId) throws PlanNotFoundException;

    PlanAssignmentDTO assignPlanToPatient(int patientId, int planId)
            throws PlanNotFoundException, PatientAlreadyAssignedException;

    List<PlanAssignmentDTO> getActiveAssignmentsByPatient(int patientId);

    List<PlanAssignmentDTO> getActiveAssignmentsByPlan(int planId);
}