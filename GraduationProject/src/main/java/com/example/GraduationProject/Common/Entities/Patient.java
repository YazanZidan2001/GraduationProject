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
    private Long patientId;

    @Column(name = "blood_type", nullable = true)
    @Enumerated(EnumType.STRING)
    private BloodTypes bloodType; // Using enum directly, no foreign key needed

    @Column(name = "gender", nullable = false)
    @NotNull(message = "Gender cannot be blank")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "height", nullable = true)
    private Float height;

    @Column(name = "weight", nullable = true)
    private Float weight;

    @Column(name = "remarks", nullable = true)
    private String remarks;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_id")
    private User user;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "blood_type", referencedColumnName = "blood_type", insertable = false, updatable = false)
//    private BloodType bloodTypeEntity;
}
