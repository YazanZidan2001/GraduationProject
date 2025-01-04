package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.DoctorClinicId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "doctor_clinic")
@IdClass(DoctorClinicId.class)  // Specify the composite key class
public class DoctorClinic {

    @Id
    @Column(name = "doctor_id")
    @NotNull(message = "Doctor ID cannot be blank")
    private Long doctorId; // Consistent use of Long

    @Id
    @Column(name = "clinic_id")
    @NotNull(message = "Clinic ID cannot be blank")
    private Long clinicId;

    @Column(name = "is_need_interval", nullable = true)
    @Builder.Default
    private boolean isNeedInterval= true;

    @Column(name = "time_interval", nullable = true)
    private Integer timeInterval;

    @Column(name = "is_Active", nullable = true)
    @Builder.Default
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_id", insertable = false, updatable = false)
    private Clinic clinic;
}
