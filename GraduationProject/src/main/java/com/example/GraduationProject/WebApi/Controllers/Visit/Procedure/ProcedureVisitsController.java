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
    public ResponseEntity<?> addMultipleProcedureVisits(
            @RequestBody List<ProcedureVisits> procedureVisitsList,
            HttpServletRequest request) {
        try {
            // 1) Validate user (Only doctors are allowed)
            String token = authenticationService.extractToken(request);
            User user = authenticationService.extractUserFromToken(token);
            validateLoggedInDoctor(user);

            // 2) Validate request body
            if (procedureVisitsList == null || procedureVisitsList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Procedure visits list cannot be null or empty.");
            }

            for (int i = 0; i < procedureVisitsList.size(); i++) {
                ProcedureVisits procedureVisits = procedureVisitsList.get(i);

                if (procedureVisits.getVisitID() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Visit ID is required for procedure visit at index " + i);
                }

                // Set doctor ID (user ID)
                procedureVisits.setUserID(user.getUserID());
            }

            // 3) Call service to add multiple procedure visits
            procedureVisitsService.addMultipleProcedureVisits(procedureVisitsList);

            return ResponseEntity.status(HttpStatus.CREATED).body("Multiple Procedure Visits added successfully.");

        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found: " + ex.getMessage());
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }
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


    /**
     * GET /procedure/my
     * Retrieves all procedures for the logged-in patient.
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyProcedures(HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientId = user.getPatient().getPatientId();
        List<ProcedureVisits> procedures = procedureVisitsService.getProceduresForPatient(patientId);

        if (procedures.isEmpty()) {
            return ResponseEntity.ok("No procedures found.");
        }
        return ResponseEntity.ok(procedures);
    }

    /**
     * GET /procedure/my/visit/{visitId}
     * Retrieves all procedures for a specific visit for the logged-in patient.
     */
    @GetMapping("/my/visit/{visitId}")
    public ResponseEntity<?> getProceduresForVisit(
            @PathVariable Long visitId,
            HttpServletRequest request
    ) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientId = user.getPatient().getPatientId();
        List<ProcedureVisits> procedures = procedureVisitsService.getProceduresForVisitForPatient(visitId, patientId);

        if (procedures.isEmpty()) {
            return ResponseEntity.ok("No procedures found for this visit.");
        }
        return ResponseEntity.ok(procedures);
    }

    /**
     * GET /procedure/my/visit/{visitId}/procedure/{procedureVisitId}
     * Retrieves a specific procedure for a specific visit for the logged-in patient.
     */
    @GetMapping("/my/visit/{visitId}/procedure/{procedureVisitId}")
    public ResponseEntity<?> getProcedureForVisit(
            @PathVariable Long visitId,
            @PathVariable Long procedureVisitId,
            HttpServletRequest request
    ) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientId = user.getPatient().getPatientId();
        ProcedureVisits procedure = procedureVisitsService.getProcedureForVisitForPatient(visitId, procedureVisitId, patientId);

        if (procedure == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Procedure not found for this visit.");
        }
        return ResponseEntity.ok(procedure);
    }

    /**
     * GET /procedure/my/{procedureVisitId}
     * Retrieves a specific procedure for the logged-in patient by procedure ID.
     */
    @GetMapping("/my/{procedureVisitId}")
    public ResponseEntity<?> getProcedureById(
            @PathVariable Long procedureVisitId,
            HttpServletRequest request
    ) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientId = user.getPatient().getPatientId();
        ProcedureVisits procedure = procedureVisitsService.getProcedureByIdForPatient(procedureVisitId, patientId);

        if (procedure == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Procedure not found.");
        }
        return ResponseEntity.ok(procedure);
    }

    /**
     * GET /procedure/patient/{patientId}/procedure/{procedureVisitId}
     * Retrieves a specific procedure for a specific patient (accessible by doctors).
     */
    @GetMapping("/patient/{patientId}/procedure/{procedureVisitId}")
    public ResponseEntity<?> getProcedureForPatient(
            @PathVariable Long patientId,
            @PathVariable Long procedureVisitId,
            HttpServletRequest request
    ) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        ProcedureVisits procedure = procedureVisitsService.getProcedureByIdForPatient(procedureVisitId, patientId);

        if (procedure == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Procedure not found for this patient.");
        }
        return ResponseEntity.ok(procedure);
    }

    /**
     * GET /procedure/patient/{patientId}/visit/{visitId}
     * Retrieves all procedures for a specific visit for a specific patient (accessible by doctors).
     */
    @GetMapping("/patient/{patientId}/visit/{visitId}")
    public ResponseEntity<?> getProceduresForVisitForPatient(
            @PathVariable Long patientId,
            @PathVariable Long visitId,
            HttpServletRequest request
    ) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        List<ProcedureVisits> procedures = procedureVisitsService.getProceduresForVisitForPatient(visitId, patientId);
        if (procedures.isEmpty()) {
            return ResponseEntity.ok("No procedures found for this visit.");
        }
        return ResponseEntity.ok(procedures);
    }



}
