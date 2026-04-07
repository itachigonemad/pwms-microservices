package com.pwms.progress.dto;

import com.pwms.progress.model.Progress.ActivityStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateDTO {
    private int            activityId;
    private ActivityStatus status;      // DONE / PENDING / SKIPPED
}
