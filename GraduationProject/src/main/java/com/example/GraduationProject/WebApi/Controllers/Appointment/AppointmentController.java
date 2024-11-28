package com.example.GraduationProject.WebApi.Controllers.Appointment;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Appointment;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AppointmentService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("appointments")
@RequiredArgsConstructor
public class AppointmentController extends SessionManagement {

    private final AppointmentService appointmentService;
    private final AuthenticationService service;

    @PostMapping
    public ResponseEntity<String> addAppointment(@RequestBody Appointment appointment, HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInDoctor(user);
        appointmentService.addAppointment(appointment);
        return ResponseEntity.ok("Appointment added successfully");
    }

    @PutMapping("/{appointmentID}/{doctorID}/{patientID}/{clinicID}")
    public ResponseEntity<String> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable Long appointmentID,
            @PathVariable Long doctorID,
            @PathVariable Long patientID,
            @PathVariable Long clinicID,
            HttpServletRequest request) throws NotFoundException, UserNotFoundException {
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInDoctor(user);
        appointmentService.updateAppointment(appointment, appointmentID, doctorID, patientID, clinicID);
        return ResponseEntity.ok("Appointment updated successfully");
    }

    @GetMapping("/{appointmentID}/{doctorID}/{patientID}/{clinicID}")
    public ResponseEntity<Appointment> getAppointment(
            @PathVariable Long appointmentID,
            @PathVariable Long doctorID,
            @PathVariable Long patientID,
            @PathVariable Long clinicID,
            HttpServletRequest request) throws NotFoundException, UserNotFoundException {
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);
        Appointment appointment = appointmentService.findAppointment(appointmentID, doctorID, patientID, clinicID);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/doctor/{doctorID}")
    public ResponseEntity<PaginationDTO<Appointment>> getAppointmentsByDoctorID(
            @PathVariable Long doctorID,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInDoctor(user);
        PaginationDTO<Appointment> appointments = appointmentService.findByDoctorID(doctorID, page, size);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/patient/{patientID}")
    public ResponseEntity<PaginationDTO<Appointment>> getAppointmentsByPatientID(
            @PathVariable String patientID,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {

        // Validate the logged-in user
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);
        Long patientId = user.getPatient().getPatientId();

        // Fetch appointments for the given patient ID
        PaginationDTO<Appointment> appointments = appointmentService.findByPatientID(patientID, page, size);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/searchByPatient")
    public ResponseEntity<PaginationDTO<Appointment>> searchAppointmentsByPatient(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {

        // Validate the logged-in user
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);

        // Fetch appointments based on the search criteria
        PaginationDTO<Appointment> paginationDTO = appointmentService.findByPatientID(search, page, size);

        // Return only the list of appointments
        return ResponseEntity.ok(paginationDTO);
    }


    @GetMapping("/date")
    public ResponseEntity<PaginationDTO<Appointment>> getAppointmentsByDate(
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);
        PaginationDTO<Appointment> appointments = appointmentService.findByAppointmentDate(date, page, size);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/clinic/{clinicID}")
    public ResponseEntity<PaginationDTO<Appointment>> getAppointmentsByClinicID(
            @PathVariable Long clinicID,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInDoctor(user);
        PaginationDTO<Appointment> appointments = appointmentService.findByClinicID(clinicID, page, size);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/appointmentID/{appointmentID}")
    public ResponseEntity<PaginationDTO<Appointment>> getAppointmentsByAppointmentID(
            @PathVariable Long appointmentID,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);
        PaginationDTO<Appointment> appointments = appointmentService.findByAppointmentID(appointmentID, page, size);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping
    public ResponseEntity<PaginationDTO<Appointment>> getAllAppointments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PaginationDTO<Appointment> appointments = appointmentService.getAllAppointments(page, size);
        return ResponseEntity.ok(appointments);
    }
}
