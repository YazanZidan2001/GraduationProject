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
@Table(name = "x_ray")
public class XRay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "xray_id", nullable = false, unique = true)
    private Long xrayId;

    @NotNull(message = "Visit ID cannot be blank")
    @Column(name = "visit_id", nullable = false)
    private Long visitId;

    @NotNull(message = "Patient ID cannot be blank")
    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @NotNull(message = "Title cannot be blank")
    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "xray_time", nullable = false)
    private LocalTime xrayTime;

    @Column(name = "xray_date", nullable = false)
    private LocalDate xrayDate;

    @Column(name = "xray_details", columnDefinition = "TEXT")
    private String xrayDetails;

    @Column(name = "xray_result", columnDefinition = "TEXT")
    private String xrayResult;

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;

    @Column(name = "insert_time", nullable = false)
    private LocalDateTime insertTime;

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
    // Optional: Access patient data from the visit
    @Transient
    public Long getAssociatedPatientId() {
        return this.visit != null ? this.visit.getPatientId() : null;
    }
}
