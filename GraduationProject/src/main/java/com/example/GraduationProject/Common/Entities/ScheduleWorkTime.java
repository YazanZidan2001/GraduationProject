package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.DoctorClinicId;
import com.example.GraduationProject.Common.CompositeKey.ScheduleWorkTimeId;
import com.example.GraduationProject.Common.Enums.DaysOfWeek;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Schedule_work_time")
@IdClass(ScheduleWorkTimeId.class)
public class ScheduleWorkTime {


    @Id
    @Column(name = "schedule_id")
    @NotNull(message = "scheduleI ID cannot be blank")
    private Long scheduleId;

    @Id
    @Column(name = "doctor_id")
    @NotNull(message = "Doctor ID cannot be blank")
    private Long doctorId;

    @Id
    @Column(name = "clinic_id")
    @NotNull(message = "Clinic ID cannot be blank")
    private Long clinicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    @NotNull(message = "Days of Week cannot be blank")
    private List<DaysOfWeek> daysOfWeek;


    @Column(name = "start_time")
    @NotNull(message = "Start time cannot be blank")
    private LocalTime startTime;

    @Column(name = "end_time")
    @NotNull(message = "End time cannot be blank")
    private LocalTime endTime;

    @Column(name = "from_date")
    @NotNull(message = "From date cannot be blank")
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = true)
    private LocalDate toDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clinic_id", insertable = false, updatable = false)
    private Clinic clinic;
}
