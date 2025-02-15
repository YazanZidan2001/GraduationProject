package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import com.example.GraduationProject.Common.Entities.AppointmentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentNotificationRepository extends JpaRepository<AppointmentNotification, AppointmentCompositeKey> {

    // Find notifications that need to be sent (date and time is 24 hours from now)
    List<AppointmentNotification> findByDateAndTimeAndIsSendFalse(LocalDate date, String time);

    @Query("""
        SELECT an FROM AppointmentNotification an 
        JOIN Appointment a 
        ON an.appointmentID = a.appointmentID
        AND an.doctorID = a.doctorID
        AND an.patientID = a.patientID
        AND an.clinicID = a.clinicID
        WHERE an.patientID = :patientID 
        AND an.isSend = false 
        AND a.isDone = false 
        AND a.isCancelled = false
        AND a.appointmentDate >= CURRENT_DATE
    """)
    List<AppointmentNotification> findByPatientIDAndIsSendFalseAndAppointmentActive(Long patientID);

    Optional<AppointmentNotification> findByAppointmentID(Long appointmentID);



}
