package com.example.GraduationProject.WebApi.Controllers.Appointment;

import com.example.GraduationProject.Common.Entities.AppointmentNotification;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Repositories.AppointmentNotificationRepository;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/appointments/notifications")
@RequiredArgsConstructor
public class AppointmentNotificationController {
    private final AppointmentNotificationRepository notificationRepository;
    private final AuthenticationService authenticationService;

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentNotification>> getMyNotifications(HttpServletRequest httpServletRequest) {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        List<AppointmentNotification> notifications = notificationRepository
                .findByPatientIDAndIsSendFalseAndAppointmentActive(user.getUserID());

        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{appointmentId}/mark-as-sent")
    public ResponseEntity<Void> markNotificationAsSent(@PathVariable Long appointmentId) {
        AppointmentNotification notification = notificationRepository.findByAppointmentID(appointmentId)
                .orElseThrow(() -> new RuntimeException("Notification not found for appointment ID: " + appointmentId));

        notification.setIsSend(true);
        notificationRepository.save(notification);

        return ResponseEntity.ok().build();
    }


}
