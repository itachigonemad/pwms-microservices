package com.pwms.wellnessplan.dto;

import lombok.*;
import java.time.LocalDate;

// Returned instead of raw PlanAssignment entity
// Includes planId and planName explicitly — no lazy load needed
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanAssignmentDTO {
    private int       assignmentId;
    private int       patientId;
    private int       planId;       // extracted from plan entity
    private String    planName;     // extracted from plan entity
    private LocalDate assignedDate;
    private String    status;
}