package com.pwms.progress.dto;

import lombok.*;

// Mirror of wellnessplan-service ActivityDTO
// Must match field names exactly — used to deserialize HTTP response
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private int    activityId;
    private int    planId;
    private String activityName;
}