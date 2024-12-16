package com.example.GraduationProject.Common.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lab_test")
public class LabTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id", nullable = false, unique = true)
    private Long testId;

    @NotNull(message = "Visit ID cannot be blank")
    @Column(name = "visit_id", nullable = false)
    private Long visitId;

    @NotNull(message = "Patient ID cannot be blank")
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @NotNull(message = "Title cannot be blank")
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "test_time", nullable = false)
    private LocalTime testTime;

    @Column(name = "test_date", nullable = false)
    private LocalDate testDate;

    @Column(name = "test_details", columnDefinition = "TEXT")
    private String testDetails;

    @Column(name = "test_result", columnDefinition = "TEXT")
    private String testResult;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @Column(name = "insert_time", nullable = false)
    private LocalDateTime insertTime;

    @Column(name = "clinic_id", nullable = false)
    private Long clinicId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    // Automatically populate insertTime before persisting
    @PrePersist
    protected void onInsert() {
        this.insertTime = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "visit_id", referencedColumnName = "visit_id", insertable = false, updatable = false),
            @JoinColumn(name = "clinic_id", referencedColumnName = "clinic_id", insertable = false, updatable = false),
            @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", insertable = false, updatable = false),
            @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", insertable = false, updatable = false)
    })
    private Visit visit;

}
