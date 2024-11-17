package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import com.example.GraduationProject.Common.Entities.Appointment;
import com.example.GraduationProject.Common.Entities.AppointmentNotification;
import com.example.GraduationProject.Core.Repositories.AppointmentNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentNotificationService {

    private final AppointmentNotificationRepository notificationRepository;

    public void createNotification(Appointment appointment) {
        AppointmentNotification notification = AppointmentNotification.builder()
                .appointmentID(appointment.getAppointmentID())
                .doctorID(appointment.getDoctorID())
                .patientID(appointment.getPatientID())
                .clinicID(appointment.getClinicID())
                .date(appointment.getAppointmentDate())
                .time(appointment.getAppointmentTime())
                .isSend(false)
                .build();

        notificationRepository.save(notification);
    }

    // This will run every hour to check for notifications to be sent
    @Scheduled(fixedRate = 3600000)
    public void sendNotifications() {
        LocalDate targetDate = LocalDate.now().plusDays(1);
        LocalTime targetTime = LocalTime.now();

        List<AppointmentNotification> notifications = notificationRepository
                .findByDateAndTimeAndIsSendFalse(targetDate, targetTime);

        for (AppointmentNotification notification : notifications) {
            // Send notification logic here (email, SMS, etc.)
            notification.setIsSend(true);
            notificationRepository.save(notification);
        }
    }
}
