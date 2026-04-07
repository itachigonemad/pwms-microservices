package com.pwms.progress.client;

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

    public void notifyActivityAppreciation(int patientId, int planId, String activityName) {
        String url = notificationUrl +
                "/api/notifications/internal/activity-appreciation" +
                "?patientId="    + patientId +
                "&planId="       + planId +
                "&activityName=" + activityName;
        post(url, "activity appreciation");
    }

    public void notifyActivityReminder(int patientId, int planId, String activityName) {
        String url = notificationUrl +
                "/api/notifications/internal/activity-reminder" +
                "?patientId="    + patientId +
                "&planId="       + planId +
                "&activityName=" + activityName;
        post(url, "activity reminder");
    }

    public void notifyPlanCompleted(int patientId, int planId, int adminId) {
        String url = notificationUrl +
                "/api/notifications/internal/plan-completed" +
                "?patientId=" + patientId +
                "&planId="    + planId +
                "&adminId="   + adminId;
        post(url, "plan completed");
    }

    public void notifyWeeklySummary(int patientId, int planId, double completionPct) {
        String url = notificationUrl +
                "/api/notifications/internal/weekly-summary" +
                "?patientId="     + patientId +
                "&planId="        + planId +
                "&completionPct=" + completionPct;
        post(url, "weekly summary");
    }

    public void notifyAppointmentReminder(int patientId, int planId) {
        String url = notificationUrl +
                "/api/notifications/internal/appointment-reminder" +
                "?patientId=" + patientId +
                "&planId="    + planId;
        post(url, "appointment reminder");
    }

    // Shared POST helper — notification failure never breaks caller
    private void post(String url, String type) {
        try {
            restTemplate.postForObject(url, null, Void.class);
            log.info("Notification sent: {}", type);
        } catch (Exception e) {
            log.error("Failed to send {} notification: {}", type, e.getMessage());
        }
    }
}