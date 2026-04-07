package com.pwms.notification.controller;

import com.pwms.notification.dto.NotificationDTO;
import com.pwms.notification.exception.NotificationNotFoundException;
import com.pwms.notification.interfaces.NotificationIntf;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationIntf notificationService;

    // GET /api/notifications/patient/{patientId}
    // All notifications for a patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<NotificationDTO>> getForPatient(
            @PathVariable int patientId) throws NotificationNotFoundException {
        return ResponseEntity.ok(
                notificationService.getNotificationsForPatient(patientId));
    }

    // GET /api/notifications/patient/{patientId}/unread
    // Unread notifications for a patient
    @GetMapping("/patient/{patientId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadForPatient(
            @PathVariable int patientId) throws NotificationNotFoundException {
        return ResponseEntity.ok(
                notificationService.getUnreadForPatient(patientId));
    }

    // GET /api/notifications/admin/{adminId}
    // All notifications for admin
    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<NotificationDTO>> getForAdmin(
            @PathVariable int adminId) throws NotificationNotFoundException {
        return ResponseEntity.ok(
                notificationService.getNotificationsForAdmin(adminId));
    }

    // GET /api/notifications/admin/{adminId}/unread
    // Unread notifications for admin
    @GetMapping("/admin/{adminId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadForAdmin(
            @PathVariable int adminId) throws NotificationNotFoundException {
        return ResponseEntity.ok(
                notificationService.getUnreadForAdmin(adminId));
    }

    // PATCH /api/notifications/{notificationId}/read
    // Mark a notification as read
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<NotificationDTO> markAsRead(
            @PathVariable int notificationId) throws NotificationNotFoundException {
        return ResponseEntity.ok(
                notificationService.markAsRead(notificationId));
    }
}