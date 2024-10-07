package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.ExperienceId;
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
@IdClass(ExperienceId.class)  // Use the composite key class
public class Experience {

    @Id
    @Column(name = "experience_id")
    private Long experienceId;

    @Id
    @Column(name = "doctor_id")
    @NotNull(message = "Doctor ID cannot be blank")
    private Long doctorId;

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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;
}
