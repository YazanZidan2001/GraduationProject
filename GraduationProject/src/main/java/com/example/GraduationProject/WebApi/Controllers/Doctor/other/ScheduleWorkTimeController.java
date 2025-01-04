package com.example.GraduationProject.WebApi.Controllers.Doctor.other;

import com.example.GraduationProject.Common.Entities.ScheduleWorkTime;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.ScheduleWorkTimeService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/doctor/schedule")
@RequiredArgsConstructor
public class ScheduleWorkTimeController extends SessionManagement {

    private final ScheduleWorkTimeService scheduleWorkTimeService;
    private final AuthenticationService authenticationService;

    private static final Logger logger = LoggerFactory.getLogger(ScheduleWorkTimeController.class);

    @PostMapping
    public ResponseEntity<?> addOrUpdateWorkSchedule(
            @RequestBody ScheduleWorkTime schedule,
            HttpServletRequest request) throws UserNotFoundException {

        // Validate logged-in user
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);


        try {
            scheduleWorkTimeService.addOrUpdateWorkSchedule(schedule);
            return ResponseEntity.ok("Work schedule saved successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save work schedule: " + ex.getMessage());
        }
    }

    /**
     * Get the work schedule for a specific date.
     */
    @GetMapping("/{date}")
    public ResponseEntity<ScheduleWorkTime> getWorkScheduleByDate(
            @PathVariable LocalDate date,
            HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        // Validate the logged-in doctor
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Fetch the work schedule
        Long doctorId = user.getUserID();
        ScheduleWorkTime schedule = scheduleWorkTimeService.getWorkScheduleByDate(doctorId, date);
        return ResponseEntity.ok(schedule);
    }

    /**
     * Get available and reserved slots for a specific date.
     */
    @GetMapping("/{date}/slots")
    public ResponseEntity<List<String>> getSlotsForDate(
            @PathVariable LocalDate date,
            @RequestParam List<String> reservedSlots,
            @RequestParam int interval,
            HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        // Validate the logged-in doctor
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Fetch the slots
        Long doctorId = user.getUserID();
        List<String> slots = scheduleWorkTimeService.getSlotsForDate(doctorId, date, reservedSlots, interval);
        return ResponseEntity.ok(slots);
    }

    /**
     * Get all work schedules for a doctor and clinic.
     */
    @GetMapping("/{clinicId}/all")
    public ResponseEntity<List<ScheduleWorkTime>> getSchedulesByDoctorAndClinic(
            @PathVariable Long clinicId,
            HttpServletRequest request) throws UserNotFoundException {
        // Validate the logged-in doctor
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Fetch schedules
        Long doctorId = user.getUserID();
        List<ScheduleWorkTime> schedules = scheduleWorkTimeService.getSchedulesByDoctorAndClinic(doctorId, clinicId);
        return ResponseEntity.ok(schedules);
    }
}
