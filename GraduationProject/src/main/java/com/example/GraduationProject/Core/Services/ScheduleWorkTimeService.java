package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.ScheduleWorkTime;
import com.example.GraduationProject.Common.CompositeKey.ScheduleWorkTimeId;
import com.example.GraduationProject.Common.Entities.ScheduleWorkTimeDay;
import com.example.GraduationProject.Common.Enums.DaysOfWeek;
import com.example.GraduationProject.Core.Repositories.ScheduleWorkTimeDayRepository;
import com.example.GraduationProject.Core.Repositories.ScheduleWorkTimeRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleWorkTimeService {

    private final ScheduleWorkTimeRepository scheduleWorkTimeRepository;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleWorkTimeService.class);
    private final ScheduleWorkTimeDayRepository scheduleWorkTimeDayRepository; // Injected here

    /**
     * Add or update a doctor's work schedule.
     */
    @Transactional
    public void addOrUpdateWorkSchedule(ScheduleWorkTime scheduleWorkTime) {
        Long newScheduleId = scheduleWorkTimeRepository.findTopByOrderByScheduleIdDesc()
                .map(ScheduleWorkTime::getScheduleId)
                .orElse(0L) + 1;

        scheduleWorkTime.setScheduleId(newScheduleId);

//        // Ensure daysOfWeek list is not empty before saving
//        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
//            throw new IllegalArgumentException("At least one workday must be specified.");
//        }
//
//        // Save the schedule first
//        scheduleWorkTimeRepository.save(scheduleWorkTime);
//
//        // Save work days for the schedule
//        List<ScheduleWorkTimeDay> scheduleDays = new ArrayList<>();
//        for (DaysOfWeek day : daysOfWeek) {
//            ScheduleWorkTimeDay scheduleDay = new ScheduleWorkTimeDay();
//            scheduleDay.setScheduleWorkTime(scheduleWorkTime);
//            scheduleDay.setDayOfWeek(day);
//            scheduleDays.add(scheduleDay);
//        }

        // Save days into schedule_work_time_days table
        scheduleWorkTimeRepository.save(scheduleWorkTime);
    }





    /**
     * Find a schedule by doctor ID and date.
     */
    @Transactional(readOnly = true)
    public ScheduleWorkTime getWorkScheduleByDate(Long doctorId, LocalDate date) throws NotFoundException {
        return scheduleWorkTimeRepository.findByDoctorIdAndDate(doctorId, date)
                .orElseThrow(() -> new NotFoundException("No schedule found for the given date"));
    }

    /**
     * Generate schedule slots based on start time, end time, and interval.
     */
    public List<String> generateScheduleSlots(LocalTime startTime, LocalTime endTime, int interval) {
        List<String> slots = new ArrayList<>();
        LocalTime current = startTime;
        while (current.isBefore(endTime)) {
            LocalTime next = current.plusMinutes(interval);
            slots.add(current + " - " + next);
            current = next;
        }
        return slots;
    }

    /**
     * Get the available and reserved slots for a specific date.
     */
    @Transactional(readOnly = true)
    public List<String> getSlotsForDate(Long doctorId, LocalDate date, List<String> reservedSlots, int interval) throws NotFoundException {
        ScheduleWorkTime schedule = getWorkScheduleByDate(doctorId, date);

        // Generate time slots
        List<String> slots = generateScheduleSlots(schedule.getStartTime(), schedule.getEndTime(), interval);

        // Mark slots as "Available" or "Reserved"
        List<String> result = new ArrayList<>();
        for (String slot : slots) {
            if (reservedSlots.contains(slot)) {
                result.add(slot + " (Reserved)");
            } else {
                result.add(slot + " (Available)");
            }
        }
        return result;
    }

    /**
     * Get all work schedules for a doctor and clinic.
     */
    @Transactional(readOnly = true)
    public List<ScheduleWorkTime> getSchedulesByDoctorAndClinic(Long doctorId, Long clinicId) {
        return scheduleWorkTimeRepository.findByDoctorIdAndClinicId(doctorId, clinicId);
    }
}
