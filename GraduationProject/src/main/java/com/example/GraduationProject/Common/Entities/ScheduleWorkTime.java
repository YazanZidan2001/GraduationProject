package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.ScheduleWorkTimeId;
import com.example.GraduationProject.Common.Converters.DaysOfWeekConverter;
import com.example.GraduationProject.Common.Enums.DaysOfWeek;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    // Store multiple DaysOfWeek as a single comma-separated string in the DB
    @Convert(converter = DaysOfWeekConverter.class)
    @Column(name = "days_of_week", nullable = false)
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
