package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.PatientDiseaseId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patient_disease")
@IdClass(PatientDiseaseId.class)  // Specify the composite key class
public class PatientDisease {

    @Id
    @Column(name = "patient_id")
    @NotNull(message = "Patient ID cannot be blank")
    private Long patientId;  // The patient ID part of the composite key

    @Id
    @Column(name = "disease_name")
    @NotNull(message = "Disease name cannot be blank")
    private String diseaseName;  // The disease name part of the composite key

    @Column(name = "disease_date")
    @NotNull(message = "Disease date cannot be blank")
    private LocalDate diseaseDate;

    @Column(name = "insert_time")
    private LocalDateTime insertTime;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "remarks")
    private String remarks;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disease_name", insertable = false, updatable = false)
    private Disease disease;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
