package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_notification")
@IdClass(AppointmentCompositeKey.class)
public class AppointmentNotification {

    @Id
    @Column(name = "appointment_id")
    private Long appointmentID;

    @Id
    @Column(name = "doctor_id")
    private Long doctorID;

    @Id
    @Column(name = "patient_id")
    private Long patientID;

    @Id
    @Column(name = "clinic_id")
    private Long clinicID;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    private String time;

    @Column(name = "is_send", nullable = false)
    private Boolean isSend = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "appointment_id", referencedColumnName = "appointment_id", insertable = false, updatable = false),
            @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", insertable = false, updatable = false),
            @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", insertable = false, updatable = false),
            @JoinColumn(name = "clinic_id", referencedColumnName = "clinic_id", insertable = false, updatable = false)
    })
    private Appointment appointment;
}
