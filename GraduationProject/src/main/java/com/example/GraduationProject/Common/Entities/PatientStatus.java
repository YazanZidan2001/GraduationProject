package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.PatientStatusId;
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
@Table(name = "patient_status")
@IdClass(PatientStatusId.class)  // Specify the composite key class
public class PatientStatus {

    @Id
    @Column(name = "patient_id")
    @NotNull(message = "Patient ID cannot be blank")
    private Long patientId;  // The patient ID part of the composite key

    @Id
    @Column(name = "description")
    @NotNull(message = "Description cannot be blank")
    private String description;  // The description part of the composite key

    @Column(name = "insert_time")
    private LocalDateTime insertTime;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
