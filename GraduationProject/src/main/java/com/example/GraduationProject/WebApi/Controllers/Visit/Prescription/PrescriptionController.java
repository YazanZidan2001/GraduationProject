package com.example.GraduationProject.WebApi.Controllers.Visit.Prescription;


import com.example.GraduationProject.Common.Entities.Prescription;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.PrescriptionService;
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
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController extends SessionManagement {

    private final PrescriptionService prescriptionService;
    private final AuthenticationService authenticationService;


    @PostMapping
    public ResponseEntity<String> createPrescription(
            @RequestBody List<Prescription> prescriptionRequests,
            HttpServletRequest request
    ) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Set the userId on each prescription in the list
        for (Prescription p : prescriptionRequests) {
            p.setUserId(user.getUserID());
            p.setActive(true);
        }

        // Pass the entire list to the service
         prescriptionService.createPrescription(prescriptionRequests);

        // Return them all (could return a custom response object if you prefer)
        return ResponseEntity.status(HttpStatus.CREATED).body("Multiple Prescription added successfully");
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

