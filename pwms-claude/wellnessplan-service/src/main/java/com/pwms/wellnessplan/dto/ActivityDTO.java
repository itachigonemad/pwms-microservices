package com.pwms.wellnessplan.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private int    activityId;
    private String activityName;
}