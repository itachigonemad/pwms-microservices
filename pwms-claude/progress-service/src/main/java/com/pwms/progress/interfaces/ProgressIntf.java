package com.pwms.progress.interfaces;

import com.pwms.progress.dto.*;
import com.pwms.progress.exception.*;

import java.util.List;

public interface ProgressIntf {

    // Called when admin assigns a plan — seeds PENDING rows
    void initializeProgress(int patientId, int planId)
            throws ProgressAlreadyExistsException;

    // Patient updates a single activity status
    ProgressResponseDTO updateStatus(int patientId, StatusUpdateDTO dto)
            throws ProgressNotFoundException;

    // Get full progress list for a patient (with activity names merged)
    List<ProgressResponseDTO> getProgressByPatient(int patientId)
            throws ProgressNotFoundException;

    // Get progress for a specific patient + plan
    List<ProgressResponseDTO> getProgressByPatientAndPlan(int patientId, int planId)
            throws ProgressNotFoundException;

    // Daily summary — used by report module and dashboard
    ProgressSummaryDTO getDailySummary(int patientId, int planId)
            throws ProgressNotFoundException;
}