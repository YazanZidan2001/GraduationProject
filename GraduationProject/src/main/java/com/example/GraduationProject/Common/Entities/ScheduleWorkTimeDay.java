package com.example.GraduationProject.Common.Entities;

import com.example.GraduationProject.Common.CompositeKey.ScheduleWorkTimeId;
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
@Table(name = "schedule_work_time_days")
public class ScheduleWorkTimeDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id"),
            @JoinColumn(name = "doctor_id", referencedColumnName = "doctor_id"),
            @JoinColumn(name = "clinic_id", referencedColumnName = "clinic_id")
    })
    private ScheduleWorkTime scheduleWorkTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DaysOfWeek dayOfWeek;
}

