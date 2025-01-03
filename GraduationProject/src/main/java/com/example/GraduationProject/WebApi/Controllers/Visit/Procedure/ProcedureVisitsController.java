package com.example.GraduationProject.WebApi.Controllers.Visit.Procedure;

import com.example.GraduationProject.Common.Entities.ProcedureVisits;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Enums.Role;
import com.example.GraduationProject.Core.Services.ProcedureVisitsService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/procedure")
@RequiredArgsConstructor
public class ProcedureVisitsController extends SessionManagement {

    private final ProcedureVisitsService procedureVisitsService;
    private final AuthenticationService authenticationService;

    /**
     * Add a new Procedure Visit.
     */
    @PostMapping("/add")
    public ResponseEntity<String> addProcedureVisit(
            @RequestBody ProcedureVisits procedureVisits,
            HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        // Extract user from token
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Set user ID
        procedureVisits.setUserID(user.getUserID());

        // Add the procedure visit
        procedureVisitsService.addProcedureVisit(procedureVisits);

        return ResponseEntity.status(HttpStatus.CREATED).body("Procedure visit added successfully");
    }


    @PostMapping("/procedurevisit/batch")
    public ResponseEntity<String> addMultipleProcedureVisits(@RequestBody List<ProcedureVisits> procedureVisitsList,
                                                             HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        for (ProcedureVisits procedureVisits : procedureVisitsList) {
            procedureVisits.setUserID(user.getUserID());
        }

        procedureVisitsService.addMultipleProcedureVisits(procedureVisitsList);
        return ResponseEntity.status(HttpStatus.CREATED).body("Multiple Procedure Visits added successfully");
    }




    /**
     * Get all procedures for a specific patient (accessible by doctors).
     */
    @GetMapping("/doctor/patient/{patientId}")
    public ResponseEntity<List<ProcedureVisits>> getAllProceduresForPatient(
            @PathVariable Long patientId,
            HttpServletRequest request) throws UserNotFoundException {
        // Extract token and validate the doctor
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Fetch all procedures for the patient
        List<ProcedureVisits> procedures = procedureVisitsService.getProceduresByPatientId(patientId);

        return ResponseEntity.ok(procedures);
    }

    @GetMapping
    public ResponseEntity<List<ProcedureVisits>> getAllProcedureVisits() {
        List<ProcedureVisits> procedureVisits = procedureVisitsService.getAllProcedureVisits();
        return ResponseEntity.ok(procedureVisits);
    }

    /**
     * Get all procedures by visit ID (accessible by doctors and patients).
     */
    @GetMapping("/visit/{visitID}/procedures")
    public ResponseEntity<List<ProcedureVisits>> getProceduresByVisitId(
            @PathVariable Long visitID,
            HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        // Extract user from token
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);


        // Validate the user role
        validateLoggedInPatientAndDoctor(user);
        // Determine whether the user is a doctor or patient
        if (user.getRole() == Role.DOCTOR) {
            Long doctorID = user.getUserID(); // Doctor's ID
        } else if (user.getRole() == Role.PATIENT) {
            Long patientID = user.getUserID(); // Patient's ID
        }else {
            throw new NotFoundException("Access denied. Only doctors or patients can view visits.");
        }

        // Fetch procedures by visit ID
        List<ProcedureVisits> procedures = procedureVisitsService.getProceduresByVisitId(visitID);

        return ResponseEntity.ok(procedures);
    }

}
