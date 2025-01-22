package com.example.GraduationProject.Common.Entities;
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
@Table(name = "prescription")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prescriptionId;

    // -----------------------------------------------------------
    // Relationship to "Visit", or to "Doctor" and "Patient" directly
    // -----------------------------------------------------------
    @NotNull(message = "Visit ID cannot be blank")
    @Column(name = "visit_id", nullable = false)
    private Long visitId;

    @NotNull(message = "Patient ID cannot be blank")
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @Column(name = "clinic_id", nullable = false)
    private Long clinicId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;


    // Option A: Link to Visit (which already has doctorId, patientId)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "visit_id", referencedColumnName = "visit_id", insertable = false, updatable = false),
            @JoinColumn(name = "clinic_id", referencedColumnName = "clinic_id", insertable = false, updatable = false),
            @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", insertable = false, updatable = false),
            @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", insertable = false, updatable = false)
    })
    private Visit visit;


    // -----------------------------------------------------------
    // Relationship to "MedicationsMaster"
    // -----------------------------------------------------------

    @Column(name = "medication_id", nullable = false)
    @NotNull(message = "medication Id cannot be blank")
    private Long medicationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medication_id", insertable = false, updatable = false)
    private MedicationsMaster medication;  // The chosen medication



    // -----------------------------------------------------------
    // Prescription details
    // -----------------------------------------------------------
    @Column(name = "dose_type", nullable = false)
    private String doseType;   // "pills", "liquid", "injection", etc.

    @Column(name = "dose_amount", nullable = false)
    private String doseAmount; // e.g. "500 mg", "5 ml", "1 pill"

    // Times per day can be morning/noon/evening. We can store them as booleans or explicit fields:
    @Column(name = "morning_dose", nullable = false)
    private boolean morningDose;  // if true, schedule a 7 AM notification

    @Column(name = "noon_dose", nullable = false)
    private boolean noonDose;     // if true, schedule a 2 PM notification

    @Column(name = "evening_dose", nullable = false)
    private boolean eveningDose;  // if true, schedule an 8 PM notification

    @Column(name = "day_interval", nullable = false)
    private Integer dayInterval;  // e.g. 1 = every day, 2 = every other day, etc.

    @Column(name = "total_days", nullable = false)
    private Integer totalDays;  // e.g. 7 or 30

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate; // can be computed as (startDate + #days * dayInterval) or set explicitly



    @Column(name = "is_active", nullable = false)
    private boolean isActive; // default true

    @Column(name = "insert_time", nullable = false)
    private LocalDate insertTime;  // date/time this prescription was inserted

    // Who created the record? Could store "createdBy" referencing a user
    @Column(name = "user_id")
    private Long userId; // or a relationship to User if you want

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

}

