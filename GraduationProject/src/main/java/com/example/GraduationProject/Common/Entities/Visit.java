package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.VisitCompositeKey;
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
@Table(name = "visits")
@IdClass(VisitCompositeKey.class)
public class Visit {

    @Id
    @Column(name = "visit_id", nullable = false, unique = true)
    private Long visitID;

    @Id
    @Column(name = "clinic_id", nullable = false)
    @NotNull(message = "Clinic ID cannot be blank")
    private Long clinicId;

    @Id
    @Column(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor ID cannot be blank")
    private Long doctorId;

    @Id
    @Column(name = "patient_id", nullable = false)
    @NotNull(message = "Patient ID cannot be blank")
    private Long patientId;

    @Column(name = "visit_date", nullable = false)
    @NotNull(message = "Visit date cannot be blank")
    private LocalDate visitDate;

    @Column(name = "visit_time", nullable = false)
    @NotNull(message = "Visit time cannot be blank")
    private LocalTime visitTime;

    @Column(name = "complaint", nullable = false)
    @NotNull(message = "Complaint cannot be blank")
    private String complaint;

    @Column(name = "diagnoses", nullable = true)
    private String diagnoses;

    @Column(name = "follow_up", nullable = true)
    private String followUp;

    @Column(name = "medical_leave_days", nullable = true)
    private Integer medicalLeaveDays;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_id", insertable = false, updatable = false)
    private Clinic clinic;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

}
