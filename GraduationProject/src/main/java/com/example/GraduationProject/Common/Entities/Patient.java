package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.Enums.BloodTypes;
import com.example.GraduationProject.Common.Enums.Gender;
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
@Table(name = "patient")
public class Patient {

    @Id
    @Column(name = "patient_id", nullable = false, unique = true)
    private long patientId;

    @Column(name = "blood_type", nullable = true)
    @Enumerated(EnumType.STRING)
//    @NotNull(message = "Blood type cannot be blank")
    private BloodTypes bloodType;

    @Column(name = "gender", nullable = false)
    @NotNull(message = "Gender cannot be blank")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "dateOfBirth", nullable = false)
    @NotNull(message = "Date of birth cannot be blank")
    private LocalDate dateOfBirth;

    @Column(name = "height", nullable = true)
    private Float height;

    @Column(name = "weight", nullable = true)
    private Float weight;

    @Column(name = "remarks", nullable = true)
    private String remarks;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id")
    private User user;

    // This relationship can be managed based on the bloodType
    // Remove this unless needed for a specific use case
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "blood_type", referencedColumnName = "blood_type", insertable = false, updatable = false)
    private BloodType bloodTypeEntity; // You might want to remove this if not needed
}
