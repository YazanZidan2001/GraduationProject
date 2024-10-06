package com.example.GraduationProject.WebApi.Controllers.Patient.other;

import com.example.GraduationProject.Common.Entities.PatientStatus;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Enums.Role;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.PatientStatusService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patients/status")
public class PatientStatusController extends SessionManagement {

    private final PatientStatusService patientStatusService;
    private final AuthenticationService authenticationService;

    // Add new patient status (both doctor and patient can add statuses)
    @PostMapping("/")
    public ResponseEntity<GeneralResponse> addPatientStatus(@RequestBody @Valid PatientStatus request, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);  // Ensuring only patient and doctor can perform this

        // Check if the user is a patient and trying to modify another patient's information
        if (user.getRole().equals(Role.PATIENT) && !user.getUserID().equals(request.getPatientId())) {
            throw new UserNotFoundException("You can only modify your own information.");
        }

        request.setUserId(user.getUserID());  // Set the current user's ID in the request
        patientStatusService.addPatientStatus(request);
        return ResponseEntity.ok(GeneralResponse.builder().message("Patient status added successfully").build());
    }

    // Update existing patient status by status ID
    @PutMapping("/{statusId}")
    public ResponseEntity<GeneralResponse> updatePatientStatus(@PathVariable Long statusId, @RequestBody @Valid PatientStatus request, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);  // Ensuring only patient and doctor can perform this

        // Check if the user is a patient and trying to modify another patient's information
        if (user.getRole().equals(Role.PATIENT) && !user.getUserID().equals(request.getPatientId())) {
            throw new UserNotFoundException("You can only modify your own information.");
        }

        request.setUserId(user.getUserID());  // Set the current user's ID in the request
        patientStatusService.updatePatientStatus(statusId, request);
        return ResponseEntity.ok(GeneralResponse.builder().message("Patient status updated successfully").build());
    }

    // Get all statuses by patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientStatus>> getAllStatusesByPatientId(@PathVariable Long patientId, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatientAndDoctor(user);  // Ensuring only patient and doctor can perform this

        // Check if the user is a patient and trying to view another patient's information
        if (user.getRole().equals(Role.PATIENT) && !user.getUserID().equals(patientId)) {
            throw new UserNotFoundException("You can only view your own information.");
        }

        List<PatientStatus> patientStatuses = patientStatusService.getAllStatusesByPatientId(patientId);
        return ResponseEntity.ok(patientStatuses);
    }
}
