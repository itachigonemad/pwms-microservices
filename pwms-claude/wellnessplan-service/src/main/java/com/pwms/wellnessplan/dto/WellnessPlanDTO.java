package com.pwms.wellnessplan.dto;

import lombok.*;
import java.util.List;

// Returned from controller instead of raw WellnessPlan entity
// Includes activities explicitly since they are @JsonIgnore on the entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WellnessPlanDTO {
    private int              planId;
    private String           planName;
    private List<ActivityDTO> activities;
}