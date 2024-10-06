package com.example.GraduationProject.Common.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qualification")
public class Qualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qualification_id")
    private long qualificationId;

    @Id
    @Column(name = "doctor_id")
    @NotNull(message = "Doctor ID cannot be blank")
    private Long doctorId;

    @Column(name = "name", nullable = false)
    @NotNull(message = "Name cannot be blank")
    private String name;

    @Column(name = "year_obtained")
    @NotNull(message = "Year obtained cannot be blank")
    private Integer yearObtained; // Use Integer for YEAR type in SQL

    @Column(name = "institution", nullable = false)
    @NotNull(message = "Institution cannot be blank")
    private String institution;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;
}
