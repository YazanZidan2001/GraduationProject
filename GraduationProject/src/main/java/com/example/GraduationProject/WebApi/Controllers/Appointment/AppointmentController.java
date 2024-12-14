package com.example.GraduationProject.WebApi.Controllers.Appointment;

import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Appointment;
import com.example.GraduationProject.Common.Entities.DoctorClinic;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Repositories.AppointmentRepository;
import com.example.GraduationProject.Core.Repositories.DoctorClinicRepository;
import com.example.GraduationProject.Core.Services.AppointmentService;
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

@RestController
@RequestMapping("appointments")
@RequiredArgsConstructor
public class AppointmentController extends SessionManagement {

    private final AppointmentService appointmentService;
    private final AuthenticationService authenticationService;
    private final DoctorClinicRepository doctorClinicRepository;
    private final AppointmentRepository appointmentRepository;

    @PostMapping
    public ResponseEntity<String> addAppointment(@RequestBody Appointment appointment, HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);
        appointmentService.addAppointment(appointment);
        return ResponseEntity.ok("Appointment added successfully");
    }

    @PostMapping("/appointment/doctor")
    public ResponseEntity<String> addAppointmentByDoctor(
            @RequestBody Appointment appointment,
            HttpServletRequest request) throws UserNotFoundException, NotFoundException {

        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        Long doctorId = user.getDoctor().getDoctorId();
        Long clinicId = doctorClinicRepository.findClinicIdByDoctorId(doctorId)
                .orElseThrow(() -> new NotFoundException("Clinic not found for the given doctor."));

        if (!doctorClinicRepository.existsByDoctorIdAndClinicId(doctorId, clinicId)) {
            throw new NotFoundException("Doctor is not associated with this clinic.");
        }

        appointment.setDoctorID(doctorId);
        appointment.setClinicID(clinicId);

        appointmentService.addAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body("Appointment added successfully by doctor");
    }




    @PostMapping("/appointment/patient")
    public ResponseEntity<String> addAppointmentByPatient(
            @RequestBody Appointment appointment,
            HttpServletRequest request) throws UserNotFoundException, NotFoundException {

        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientId = user.getPatient().getPatientId();
        Long doctorId = appointment.getDoctorID();
        Long clinicId = doctorClinicRepository.findClinicIdByDoctorId(doctorId)
                .orElseThrow(() -> new NotFoundException("Clinic not found for the given doctor."));

        appointment.setPatientID(patientId);
        appointment.setClinicID(clinicId);

        appointmentService.addAppointment(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body("Appointment added successfully by patient");
    }



    @PutMapping("/{appointmentID}/{doctorID}/{patientID}/{clinicID}")
    public ResponseEntity<String> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable Long appointmentID,
            @PathVariable Long doctorID,
            @PathVariable Long patientID,
            @PathVariable Long clinicID,
            HttpServletRequest request) throws NotFoundException, UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
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
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
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
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
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
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);

        // Fetch appointments for the given patient ID
        PaginationDTO<Appointment> appointments = appointmentService.findByPatientID(patientID, page, size);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointments for the logged-in patient.
     */
    @GetMapping("/patient/myAppointments")
    public ResponseEntity<PaginationDTO<Appointment>> getAppointmentsForLoggedInPatient(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        // Extract the token and validate the user
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        // Get the patient ID and fetch appointments
        Long patientID = user.getPatient().getPatientId();
        PaginationDTO<Appointment> appointments = appointmentService.findAppointmentsForLoggedInPatient(patientID, page, size);

        return ResponseEntity.ok(appointments);
    }


    @GetMapping("/searchByPatient")
    public ResponseEntity<PaginationDTO<Appointment>> searchAppointmentsByPatient(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {

        // Validate the logged-in user
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
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
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
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
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
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
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);
        PaginationDTO<Appointment> appointments = appointmentService.findByAppointmentID(appointmentID, page, size);
        return ResponseEntity.ok(appointments);
    }

    // Cancel an appointment by Patient
    @PutMapping("/cancel/patient/{appointmentID}")
    public ResponseEntity<String> cancelAppointmentByPatient(
            @PathVariable Long appointmentID,
            HttpServletRequest request) throws NotFoundException, UserNotFoundException {

        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientID = user.getPatient().getPatientId();

        // Cancel the appointment by patient
        appointmentService.cancelAppointmentByPatient(appointmentID, patientID);

        return ResponseEntity.ok("Appointment cancelled successfully");
    }

    // Cancel an appointment by Doctor
    @PutMapping("/cancel/doctor/{appointmentID}/{patientID}")
    public ResponseEntity<String> cancelAppointmentByDoctor(
            @PathVariable Long appointmentID,
            @PathVariable Long patientID,
            HttpServletRequest request) throws NotFoundException, UserNotFoundException {

        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Cancel the appointment by doctor
        appointmentService.cancelAppointmentByDoctor(appointmentID, patientID);

        return ResponseEntity.ok("Appointment cancelled successfully");
    }


    @GetMapping("/patient/cancelled")
    public ResponseEntity<PaginationDTO<Appointment>> getCancelledAppointmentsByPatient(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientID = user.getPatient().getPatientId();
        PaginationDTO<Appointment> appointments = appointmentService.getAppointmentsByStatus(patientID, true, page, size);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/patient/done")
    public ResponseEntity<PaginationDTO<Appointment>> getDoneAppointmentsByPatient(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientID = user.getPatient().getPatientId();
        PaginationDTO<Appointment> appointments = appointmentService.getAppointmentsByStatus(patientID, true, false, page, size);
        return ResponseEntity.ok(appointments);
    }



    @GetMapping("/patient/active")
    public ResponseEntity<PaginationDTO<Appointment>> getActiveAppointmentsByPatient(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientID = user.getPatient().getPatientId();
        PaginationDTO<Appointment> appointments = appointmentService.getActiveAppointmentsByPatient(patientID, page, size);
        return ResponseEntity.ok(appointments);
    }


    // Get appointments for the logged-in doctor for the current date
    @GetMapping("/doctor/today")
    public ResponseEntity<PaginationDTO<Appointment>> getTodayAppointmentsForLoggedInDoctor(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        // Extract token and validate the logged-in doctor
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Get the doctor's ID from the logged-in user's profile
        Long doctorID = user.getDoctor().getDoctorId();

        // Fetch today's appointments for the logged-in doctor
        PaginationDTO<Appointment> appointments = appointmentService.getTodayAppointmentsForDoctor(doctorID, page, size);

        // Return the appointments
        return ResponseEntity.ok(appointments);
    }

    // Get all appointments for the logged-in doctor
    @GetMapping("/doctor/all")
    public ResponseEntity<PaginationDTO<Appointment>> getAllAppointmentsForLoggedInDoctor(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) LocalDate date,
            HttpServletRequest request) throws UserNotFoundException {
        // Extract token and validate the logged-in doctor
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Get the doctor's ID from the logged-in user's profile
        Long doctorID = user.getDoctor().getDoctorId();

        // Fetch appointments for the logged-in doctor with the search criteria and date filter
        PaginationDTO<Appointment> appointments = appointmentService.searchAppointmentsForDoctor(doctorID, search, date, page, size);

        // Return the appointments
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
