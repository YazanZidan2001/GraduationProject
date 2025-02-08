package com.example.GraduationProject.Common.DTOs;
import com.example.GraduationProject.Common.Entities.ScheduleWorkTime;
import com.example.GraduationProject.Common.Enums.DaysOfWeek;

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
public class ScheduleWorkTimeRequest {
    private ScheduleWorkTime schedule;
    private List<DaysOfWeek> daysOfWeek;
}

