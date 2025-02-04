package com.example.GraduationProject.WebApi.Controllers.Visit.Prescription;


import com.example.GraduationProject.Common.Entities.Prescription;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.PrescriptionService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController extends SessionManagement {

    private final PrescriptionService prescriptionService;
    private final AuthenticationService authenticationService;


    @PostMapping
    public ResponseEntity<?> createPrescription(
            @RequestBody List<Prescription> prescriptionRequests,
            HttpServletRequest request
    ) {
        try {
            // 1) Validate user (Only doctors are allowed)
            String token = authenticationService.extractToken(request);
            User user = authenticationService.extractUserFromToken(token);
            validateLoggedInDoctor(user);

            // 2) Validate request body
            if (prescriptionRequests == null || prescriptionRequests.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Prescription list cannot be null or empty.");
            }

            for (int i = 0; i < prescriptionRequests.size(); i++) {
                Prescription p = prescriptionRequests.get(i);

                if (p.getVisitId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Visit ID is required for prescription at index " + i);
                }
                if (p.getMedicationId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Medication ID is required for prescription at index " + i);
                }
                if (p.getStartDate() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Start date is required for prescription at index " + i);
                }
                if (p.getTotalDays() == null || p.getTotalDays() <= 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Total days must be greater than 0 for prescription at index " + i);
                }
                if (p.getDayInterval() == null || p.getDayInterval() <= 0) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Day interval must be greater than 0 for prescription at index " + i);
                }

                // Set the user ID and activate the prescription
                p.setUserId(user.getUserID());
                p.setActive(true);
            }

            // 3) Call service to create prescriptions
            prescriptionService.createPrescription(prescriptionRequests);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Multiple prescriptions added successfully.");

        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }
    }



    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPrescriptionsForPatient(
            @PathVariable Long patientId,
            HttpServletRequest request
    ) throws UserNotFoundException {

        // 1) Extract user from token
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);

        // 2) Validate user is DOCTOR
        validateLoggedInDoctor(user);
        // If the user is not a doctor, throw an exception or return 403

        try {
            // 3) Fetch all prescriptions for that patient
            List<Prescription> prescriptions = prescriptionService
                    .findAllByPatient(patientId);

            if (prescriptions.isEmpty()) {
                return ResponseEntity.ok("No prescriptions found for patient ID " + patientId);
            }
            return ResponseEntity.ok(prescriptions);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving prescriptions for patient: " + ex.getMessage());
        }
    }

    /**
     * GET /prescriptions/my/all
     * Returns ALL prescriptions (active & inactive) for the logged-in patient
     */
    @GetMapping("/my/all")
    public ResponseEntity<?> getAllMyPrescriptions(HttpServletRequest request)
            throws UserNotFoundException
    {
        // 1) Ensure the user is a patient
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientId = user.getPatient().getPatientId();

        // 2) Fetch all prescriptions
        List<Prescription> results = prescriptionService.getAllMedicationsForPatient(patientId);
        if (results.isEmpty()) {
            return ResponseEntity.ok("You have no prescriptions.");
        }
        return ResponseEntity.ok(results);
    }

    /**
     * GET /prescriptions/my/active
     * Returns only active & non-old prescriptions for the logged-in patient
     */
    @GetMapping("/my/active")
    public ResponseEntity<?> getActiveMyPrescriptions(HttpServletRequest request)
            throws UserNotFoundException
    {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientId = user.getPatient().getPatientId();

        List<Prescription> results = prescriptionService.getActiveMedicationsForPatient(patientId);
        if (results.isEmpty()) {
            return ResponseEntity.ok("No active prescriptions at this time.");
        }
        return ResponseEntity.ok(results);
    }

    /**
     * GET /prescriptions/my/inactive
     * Returns only inactive  prescriptions for the logged-in patient
     */
    @GetMapping("/my/inactive")
    public ResponseEntity<?> getInactiveMyPrescriptions(HttpServletRequest request)
            throws UserNotFoundException
    {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        Long patientId = user.getPatient().getPatientId();

        List<Prescription> results = prescriptionService.getInactiveMedicationsForPatient(patientId);
        if (results.isEmpty()) {
            return ResponseEntity.ok("No inactive prescriptions found.");
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/my/old")
    public ResponseEntity<?> getOldMyPrescriptions(HttpServletRequest request)
            throws UserNotFoundException
    {
        // 1) Extract the user
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user); // ensure it's a patient

        Long patientId = user.getPatient().getPatientId(); // or user.getUserID() if they match

        try {
            // 2) Fetch old prescriptions
            List<Prescription> oldPrescriptions = prescriptionService.getOldMedicationsForPatient(patientId);

            if (oldPrescriptions.isEmpty()) {
                return ResponseEntity.ok("No old prescriptions found.");
            }
            return ResponseEntity.ok(oldPrescriptions);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving old prescriptions: " + ex.getMessage());
        }
    }

    @PutMapping("/deactivate/{prescriptionId}")
    public ResponseEntity<?> deactivateMedication(
            @PathVariable Long prescriptionId,
            HttpServletRequest request
    ) throws UserNotFoundException {
        // 1) Extract user and confirm they are a doctor
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        try {
            // 2) Deactivate the medication in the service
            prescriptionService.deactivateMedication(prescriptionId, user.getUserID());

            // 3) Return success response
            return ResponseEntity.ok("Medication (Prescription ID: " + prescriptionId + ") deactivated successfully.");

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deactivating prescription: " + ex.getMessage());
        }
    }




}

