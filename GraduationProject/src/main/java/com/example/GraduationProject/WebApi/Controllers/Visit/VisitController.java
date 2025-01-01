package com.example.GraduationProject.WebApi.Controllers.Visit;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Entities.Visit;
import com.example.GraduationProject.Common.Enums.Role;
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
    @GetMapping("/doctor/visits")
    public ResponseEntity<PaginationDTO<Visit>> getDoctorVisits(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer month, // Month parameter
            @RequestParam(required = false) Integer year,  // Year parameter
            @RequestParam(required = false) String search,
            HttpServletRequest request) throws UserNotFoundException {

        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        PaginationDTO<Visit> visits = visitService.findVisitsByDoctor(user.getUserID(), month, year, search, page, size);
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
    /**
     * Find a visit by visit ID for a doctor or patient.
     */
    @GetMapping("/visit/{visitID}")
    public ResponseEntity<Visit> getVisitByVisitId(
            @PathVariable Long visitID,
            HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);

        // Validate that the logged-in user is either a doctor or a patient
        validateLoggedInPatientAndDoctor(user);

        Long doctorID = null;
        Long patientID = null;

        // Determine whether the user is a doctor or patient
        if (user.getRole() == Role.DOCTOR) {
            doctorID = user.getUserID(); // Doctor's ID
        } else if (user.getRole() == Role.PATIENT) {
            patientID = user.getUserID(); // Patient's ID
        }else {
            throw new NotFoundException("Access denied. Only doctors or patients can view visits.");
        }

        // Call the service method to fetch the visit
        Visit visit = visitService.findVisitByVisitIdForUser(visitID, doctorID, patientID);
        return ResponseEntity.ok(visit);
    }



}
