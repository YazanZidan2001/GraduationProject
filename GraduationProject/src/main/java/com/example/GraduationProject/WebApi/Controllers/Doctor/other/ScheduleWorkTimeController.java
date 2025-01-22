package com.example.GraduationProject.WebApi.Controllers.Doctor.other;

import com.example.GraduationProject.Common.Entities.DoctorClinic;
import com.example.GraduationProject.Common.Entities.ScheduleWorkTime;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Repositories.DoctorClinicRepository;
import com.example.GraduationProject.Core.Services.DoctorClinicService;
import com.example.GraduationProject.Core.Services.ScheduleWorkTimeService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.DoctorClinicNotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/doctors/schedule")
@RequiredArgsConstructor
public class ScheduleWorkTimeController extends SessionManagement {

    private final ScheduleWorkTimeService scheduleWorkTimeService;
    private final AuthenticationService authenticationService;
    private final DoctorClinicRepository doctorClinicRepository;
    private final DoctorClinicService doctorClinicService;


    @PutMapping("/update-interval")
    public ResponseEntity<String> updateDoctorInterval(
            @RequestParam Integer interval,
            HttpServletRequest request) throws UserNotFoundException, DoctorClinicNotFoundException {

        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        doctorClinicService.updateDoctorInterval(user.getUserID(), interval);
        return ResponseEntity.ok("Interval updated successfully");
    }

    @PostMapping
    public ResponseEntity<?> addOrUpdateWorkSchedule(
            @RequestBody ScheduleWorkTime schedule,
            HttpServletRequest request) throws UserNotFoundException {

        // Validate logged-in user
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        try {
            Long doctorId = user.getUserID();

            // Retrieve the active clinic ID for the doctor
            Long clinicId = doctorClinicRepository.findByDoctorIdAndIsActiveTrue(doctorId)
                    .map(DoctorClinic::getClinicId)
                    .orElseThrow(() -> new IllegalStateException("No active clinic found for the doctor."));

            // Set the doctorId and clinicId in the schedule
            schedule.setDoctorId(doctorId);
            schedule.setClinicId(clinicId);

            // Add or update the schedule
            scheduleWorkTimeService.addOrUpdateWorkSchedule(schedule);

            return ResponseEntity.ok("Work schedule saved successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save work schedule: " + ex.getMessage());
        }
    }


    @GetMapping("/{date}")
    public ResponseEntity<ScheduleWorkTime> getWorkScheduleByDate(
            @PathVariable LocalDate date,
            HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        Long doctorId = user.getUserID();
        ScheduleWorkTime schedule = scheduleWorkTimeService.getWorkScheduleByDate(doctorId, date);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/{date}/slots")
    public ResponseEntity<List<String>> getSlotsForDate(
            @PathVariable LocalDate date,
            @RequestParam List<String> reservedSlots,
            @RequestParam int interval,
            HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        Long doctorId = user.getUserID();
        List<String> slots = scheduleWorkTimeService.getSlotsForDate(doctorId, date, reservedSlots, interval);
        return ResponseEntity.ok(slots);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ScheduleWorkTime>> getSchedulesByDoctorAndClinic(
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        Long doctorId = user.getUserID();
        // Retrieve the active clinic ID for the doctor
        Long clinicId = doctorClinicRepository.findByDoctorIdAndIsActiveTrue(doctorId)
                .map(DoctorClinic::getClinicId)
                .orElseThrow(() -> new IllegalStateException("No active clinic found for the doctor."));
        List<ScheduleWorkTime> schedules = scheduleWorkTimeService.getSchedulesByDoctorAndClinic(doctorId, clinicId);
        return ResponseEntity.ok(schedules);
    }
}
