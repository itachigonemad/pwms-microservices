package com.pwms.report.client;

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

    public void notifyReportShared(int patientId, int planId) {
        String url = notificationUrl +
                "/api/notifications/internal/report-shared" +
                "?patientId=" + patientId +
                "&planId="    + planId;
        post(url, "report shared");
    }

    public void notifyAppointmentReminder(int patientId, int planId) {
        String url = notificationUrl +
                "/api/notifications/internal/appointment-reminder" +
                "?patientId=" + patientId +
                "&planId="    + planId;
        post(url, "appointment reminder");
    }

    private void post(String url, String type) {
        try {
            restTemplate.postForObject(url, null, Void.class);
            log.info("Notification sent: {}", type);
        } catch (Exception e) {
            log.error("Failed to send {} notification: {}", type, e.getMessage());
        }
    }
}