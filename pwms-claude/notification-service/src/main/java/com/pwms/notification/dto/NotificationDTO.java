package com.pwms.notification.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private int           notificationId;
    private int           receiverId;
    private String        receiverType;
    private String        notificationType;
    private String        message;
    private Integer       patientId;
    private Integer       planId;
    private boolean       isRead;
    private LocalDateTime createdAt;
}
