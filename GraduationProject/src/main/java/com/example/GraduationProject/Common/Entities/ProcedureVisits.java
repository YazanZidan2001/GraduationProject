package com.example.GraduationProject.Common.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "procedure_visits")
public class ProcedureVisits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "procedure_visit_id", nullable = false, unique = true)
    private Long procedureVisitId;

    @Column(name = "visit_id", nullable = false)
    @NotNull(message = "Visit ID cannot be blank")
    private Long visitID;

    @Column(name = "procedure_name", nullable = false)
    @NotNull(message = "Procedure Name cannot be blank")
    private String procedureName;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID cannot be blank")
    private Long userID;

    @Column(name = "insert_time", nullable = false)
    @NotNull(message = "Insert Time cannot be blank")
    private LocalDateTime insertTime;

    @Column(name = "procedure_date", nullable = false)
    @NotNull(message = "Procedure Date cannot be blank")
    private LocalDate procedureDate;

    @Column(name = "procedure_time", nullable = false)
    @NotNull(message = "Procedure Time cannot be blank")
    private LocalTime procedureTime;

    @Column(name = "remarks", nullable = true)
    private String remarks;

    @Column(name = "clinic_id", nullable = false)
    private Long clinicId;

    @Column(name = "doctor_id", nullable = false)
    private Long doctorId;

    @Column(name = "patient_id", nullable = false)
    private Long patientId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "visit_id", referencedColumnName = "visit_id", insertable = false, updatable = false),
            @JoinColumn(name = "clinic_id", referencedColumnName = "clinic_id", insertable = false, updatable = false),
            @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id", insertable = false, updatable = false),
            @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", insertable = false, updatable = false)
    })
    private Visit visit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "procedure_name", insertable = false, updatable = false)
    private ProcedureMaster procedureMaster;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
}
