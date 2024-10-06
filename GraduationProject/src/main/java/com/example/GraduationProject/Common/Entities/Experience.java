package com.example.GraduationProject.Common.Entities;

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
@Table(name = "experience")
public class Experience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "experience_id")
    private long experienceId;

    @Column(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor ID cannot be blank")
    private Long doctorId; // Use Long if doctor_id in Doctor is Long

    @Column(name = "institution", nullable = false)
    @NotNull(message = "Institution cannot be blank")
    private String institution;

    @Column(name = "role", nullable = false)
    @NotNull(message = "Role cannot be blank")
    private String role;

    @Column(name = "start_date", nullable = false)
    @NotNull(message = "Start date cannot be blank")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;
}
