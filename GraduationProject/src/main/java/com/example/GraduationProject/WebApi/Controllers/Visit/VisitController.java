package com.example.GraduationProject.WebApi.Controllers.Visit;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Entities.Visit;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.DoctorClinicService;
import com.example.GraduationProject.Core.Services.VisitService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("visits")
@RequiredArgsConstructor
public class VisitController extends SessionManagement {

    private final VisitService visitService;
    private final AuthenticationService authenticationService;


    /**
     * Add a new visit (only doctors can add visits).
     */
    @PostMapping
    public ResponseEntity<Visit> addVisit(@RequestBody Visit visit, HttpServletRequest request)
            throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);

        // Validate that the logged-in user is a doctor
        validateLoggedInDoctor(user);

        // Extract the doctor ID from the token
        Long doctorIdFromToken = user.getUserID();

        // Call the service to add the visit and return the saved visit
        Visit savedVisit = visitService.addVisit(visit, doctorIdFromToken);

        // Return the saved visit object in the response
        return ResponseEntity.ok(savedVisit);
    }




    /**
     * Search visits by patient fields (accessible by doctors).
     */
    @GetMapping("/searchByPatient")
    public ResponseEntity<PaginationDTO<Visit>> searchVisitsByPatientFields(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        PaginationDTO<Visit> visits = visitService.searchVisitsByPatientFields(search, page, size);
        return ResponseEntity.ok(visits);
    }

    /**
     * Get visits for a specific patient (accessible by doctors).
     */
    @GetMapping("/patient/{patientID}")
    public ResponseEntity<PaginationDTO<Visit>> getVisitsForPatient(
            @PathVariable Long patientID,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        PaginationDTO<Visit> visits = visitService.findVisitsByPatientID(patientID, page, size);
        return ResponseEntity.ok(visits);
    }


    /**
     * Get all visits for the logged-in doctor.
     */
    @GetMapping("/doctor/visits")
    public ResponseEntity<PaginationDTO<Visit>> getDoctorVisits(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search, // Optional search parameter
            @RequestParam(required = false) LocalDate date, // Optional date parameter
            HttpServletRequest request) throws UserNotFoundException {

        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Call the service with filters
        PaginationDTO<Visit> visits = visitService.findVisitsByDoctor(user.getUserID(), date, search, page, size);
        return ResponseEntity.ok(visits);
    }


    /**
     * Get all visits for the logged-in patient.
     */
    @GetMapping("/patient/visits")
    public ResponseEntity<PaginationDTO<Visit>> getPatientVisits(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        PaginationDTO<Visit> visits = visitService.findVisitsByPatient(user.getUserID(), page, size);
        return ResponseEntity.ok(visits);
    }

    /**
     * Get visits for the logged-in patient.
     */
    @GetMapping("/myVisits")
    public ResponseEntity<PaginationDTO<Visit>> getVisitsForLoggedInPatient(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientID = user.getUserID(); // Assuming patient ID is the same as user ID.
        PaginationDTO<Visit> visits = visitService.findVisitsByPatientID(patientID, page, size);
        return ResponseEntity.ok(visits);
    }

    /**
     * Get visits for a specific date for the logged-in patient.
     */
    @GetMapping("/myVisitsByDate")
    public ResponseEntity<PaginationDTO<Visit>> getVisitsForLoggedInPatientByDate(
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientID = user.getUserID(); // Assuming patient ID is the same as user ID.
        PaginationDTO<Visit> visits = visitService.findVisitsByPatientAndDate(patientID, date, page, size);
        return ResponseEntity.ok(visits);
    }

    /**
     * Get visits for a specific date for a doctor.
     */
    @GetMapping("/doctor/{doctorID}/date")
    public ResponseEntity<PaginationDTO<Visit>> getVisitsForDoctorByDate(
            @PathVariable Long doctorID,
            @RequestParam LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        PaginationDTO<Visit> visits = visitService.findVisitsByDoctorAndDate(doctorID, date, page, size);
        return ResponseEntity.ok(visits);
    }
}
