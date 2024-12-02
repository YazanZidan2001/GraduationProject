package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.Enums.DiseaseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "family_disease")
public class FamilyDisease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_disease_id", nullable = false, unique = true)
    private Long familyDiseaseId;

    @Column(name = "patient_id")
    @NotNull(message = "Patient ID cannot be blank")
    private Long patientId;

    @Column(name = "family_member", nullable = false)
    @NotNull(message = "Family member cannot be blank")
    private String familyMember; // E.g., "mother", "father", "sibling"

    @Column(name = "disease_name", nullable = false)
    @NotNull(message = "Disease name cannot be blank")
    private String diseaseName;

    @Enumerated(EnumType.STRING)
    @Column(name = "disease_type", nullable = false)
    @NotNull(message = "Disease type cannot be blank")
    private DiseaseType diseaseType; // Enum for disease type

    @Column(name = "diagnosis_date", nullable = true)
    private LocalDate diagnosisDate; // Optional, when the family member was diagnosed

    @Column(name = "remarks", nullable = true)
    private String remarks; // Additional details about the family disease

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "disease_name", insertable = false, updatable = false)
    private Disease disease;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;
}
