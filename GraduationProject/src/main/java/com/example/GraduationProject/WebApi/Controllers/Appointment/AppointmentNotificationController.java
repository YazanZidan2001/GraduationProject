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
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class AppointmentNotificationController {
    private final AppointmentNotificationRepository notificationRepository;
    private final AuthenticationService authenticationService;

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentNotification>> getMyNotifications(HttpServletRequest httpServletRequest) {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        List<AppointmentNotification> notifications = notificationRepository
                .findByPatientIDAndIsSendFalse(user.getUserID());

        return ResponseEntity.ok(notifications);
    }
}
