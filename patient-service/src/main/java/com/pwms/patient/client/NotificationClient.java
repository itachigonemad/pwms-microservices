package com.pwms.patient.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationClient {

    private final RestTemplate restTemplate;

    @Value("${services.notification.url}")
    private String notificationUrl;

    // POST /api/notifications/internal/new-patient
    public void notifyNewPatientRegistered(int patientId, int adminId) {
        String url = notificationUrl +
                "/api/notifications/internal/new-patient" +
                "?patientId=" + patientId +
                "&adminId="   + adminId;
        try {
            restTemplate.postForObject(url, null, Void.class);
            log.info("Notified admin {} of new patient {}", adminId, patientId);
        } catch (Exception e) {
            // Notification failure must never break patient registration
            log.error("Failed to notify new patient registration: {}", e.getMessage());
        }
    }
}