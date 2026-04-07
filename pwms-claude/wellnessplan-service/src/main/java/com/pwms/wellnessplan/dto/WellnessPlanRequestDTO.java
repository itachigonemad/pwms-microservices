package com.pwms.wellnessplan.dto;

import lombok.*;
import java.util.List;

// Incoming request — used for POST and PUT
// Accepts activity names from the request body
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WellnessPlanRequestDTO {
    private String       planName;
    private List<String> activityNames;  // e.g. ["Walking", "Yoga", "Diet"]
}