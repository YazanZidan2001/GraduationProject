package com.example.GraduationProject.Common.Entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "patient_like_doctor")
public class patientLikeDoctor {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false, unique = true)
    private Long likeId;


    @Column(name = "doctor_id")
    @NotNull(message = "Doctor ID cannot be blank")
    private Long doctorId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;

    @Column(name = "patient_id")
    @NotNull(message = "Patient ID cannot be blank")
    private Long patientId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    @Column(name = "is_like", nullable = false)
    @NotNull(message = "isLike value cannot be null")
    private boolean isLike;

}
