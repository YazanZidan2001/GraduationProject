package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import com.example.GraduationProject.Common.Entities.AppointmentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentNotificationRepository extends JpaRepository<AppointmentNotification, AppointmentCompositeKey> {

    // Find notifications that need to be sent (date and time is 24 hours from now)
    List<AppointmentNotification> findByDateAndTimeAndIsSendFalse(LocalDate date, String time);

    List<AppointmentNotification> findByPatientIDAndIsSendFalse(Long patientID);

    Optional<AppointmentNotification> findByAppointmentID(Long appointmentID);



}
