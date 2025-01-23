package com.example.GraduationProject.WebApi.Controllers.Admin.Medication;

import com.example.GraduationProject.Common.Entities.MedicationsMaster;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.MedicationsMasterService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medications")
@RequiredArgsConstructor
public class MedicationsMasterController extends SessionManagement {

    private final MedicationsMasterService medicationsMasterService;
    private final AuthenticationService authenticationService;

    /**
     * Admin  can add a single medication
     */
    @PostMapping("/single")
    public ResponseEntity<?> addSingleMedication(
            @RequestBody MedicationsMaster medication,
            HttpServletRequest request
    ) throws UserNotFoundException {
        // 1) Check if user is admin or doctor
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInAdmin(user); // Your method to allow ADMIN/DOCTOR

        try {
            MedicationsMaster saved = medicationsMasterService.addMedication(medication);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to add medication: " + ex.getMessage());
        }
    }

    /**
     * Admin  can add multiple medications
     * (e.g. from a JSON array). 
     * If you want file-based import (CSV, etc.), adapt accordingly.
     */
    @PostMapping("/bulk")
    public ResponseEntity<?> addMultipleMedications(
            @RequestBody List<MedicationsMaster> medications,
            HttpServletRequest request
    ) throws UserNotFoundException {
        // 1) Check if user is admin or doctor
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        try {
            List<MedicationsMaster> savedList = medicationsMasterService.addMultipleMedications(medications);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedList);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to add multiple medications: " + ex.getMessage());
        }
    }

    /**
     * Get all medications with optional search 
     * (by scientificName or internationalName).
     * Admin & Doctor can see them, or you can also allow patients if you want.
     */
    @GetMapping
    public ResponseEntity<?> getMedications(
            @RequestParam(required = false) String search,
            HttpServletRequest request
    ) throws UserNotFoundException {
        // 1) Check if user is admin or doctor 
        //    (or allow patients, if you prefer)
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctorOrAdmin(user);

        try {
            List<MedicationsMaster> list = medicationsMasterService.getAllMedications(search);
            if (list.isEmpty()) {
                return ResponseEntity.ok("No medications found.");
            }
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching medications: " + ex.getMessage());
        }
    }
}
