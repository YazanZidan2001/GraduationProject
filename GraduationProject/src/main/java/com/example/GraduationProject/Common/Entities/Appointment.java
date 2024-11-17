package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "appointment")
@IdClass(AppointmentCompositeKey.class)
public class Appointment {

    @Id
    @Column(name = "appointment_id", nullable = false)
    @NotNull(message = "Appointment ID cannot be blank")
    private Long appointmentID;

    @Id
    @Column(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor ID cannot be blank")
    private Long doctorID;

    @Id
    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID cannot be blank")
    private Long patientID;

    @Id
    @Column(name = "clinic_id", nullable = false)
    @NotNull(message = "Clinic ID cannot be blank")
    private Long clinicID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_id", insertable = false, updatable = false)
    private Clinic clinic;

    @Column(name = "appointment_time", nullable = false)
    @NotNull(message = "Appointment time cannot be blank")
    private LocalTime appointmentTime;

    @Column(name = "appointment_date", nullable = false)
    @NotNull(message = "Appointment date cannot be blank")
    private LocalDate appointmentDate;

    @Column(name = "is_done", nullable = false)
    @NotNull(message = "isDone cannot be blank")
    private Boolean isDone;
}
