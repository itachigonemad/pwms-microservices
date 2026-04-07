package com.pwms.wellnessplan.repository;

import com.pwms.wellnessplan.model.PlanAssignment;
import com.pwms.wellnessplan.model.PlanAssignment.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanAssignmentRepository extends JpaRepository<PlanAssignment, Integer> {

    // All active plans for a patient
    List<PlanAssignment> findByPatientIdAndStatus(int patientId, AssignmentStatus status);

    // All patients on a given plan
    List<PlanAssignment> findByPlan_PlanIdAndStatus(int planId, AssignmentStatus status);

    // Check if patient is already assigned to this plan
    Optional<PlanAssignment> findByPatientIdAndPlan_PlanId(int patientId, int planId);
}