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
@Table(name = "dose_schedule")
public class DoseSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doseId;

    @Column(name = "medication_id", nullable = false)
    @NotNull(message = "medication Id cannot be blank")
    private Long medicationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medication_id", insertable = false, updatable = false)
    private MedicationsMaster medication;  // The chosen medication


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "prescription_id", referencedColumnName = "prescriptionId")
    private Prescription prescription;

    // The exact date/time the patient should take this dose
    @Column(name = "dose_date", nullable = false)
    private LocalDate doseDate;

    @Column(name = "dose_time", nullable = false)
    private LocalTime doseTime; // e.g. 07:00, 14:00, 20:00

    // If you want to track notification or if dose was taken
    private boolean notificationSent;
    private boolean doseTaken;

}

