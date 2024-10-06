package com.example.GraduationProject.WebApi.Controllers.Patient.other;

import com.example.GraduationProject.Common.Entities.PatientDisease;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.PatientDiseaseService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/patients/diseases")
@RequiredArgsConstructor
public class PatientDiseaseController extends SessionManagement {

    private final PatientDiseaseService patientDiseaseService;
    private final AuthenticationService authenticationService;

    // Add or update a patient's disease record (Patients can only modify their own info)
    @PostMapping("/")
    public ResponseEntity<PatientDisease> addPatientDisease(@RequestBody @Valid PatientDisease request, HttpServletRequest httpServletRequest)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        // Validate user and ensure they are only adding their own data
        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT")
                && !user.getUserID().equals(request.getPatientId())) {  // Extract patientId from the embedded ID
            throw new UserNotFoundException("Patients can only add their own information");
        }

        // Set the current user's ID in the request
        request.setUserId(user.getUserID());

        // Save the patient disease information
        PatientDisease response = patientDiseaseService.addPatientDisease(request);
        return ResponseEntity.ok(response);
    }

    // Update an existing patient disease record (Patients can only modify their own info)
    @PutMapping("/update")
    public ResponseEntity<PatientDisease> updatePatientDisease(@RequestBody @Valid PatientDisease request, HttpServletRequest httpServletRequest)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        // Validate user and ensure they are only updating their own data
        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT")
                && !user.getUserID().equals(request.getPatientId())) {  // Extract patientId from the embedded ID
            throw new UserNotFoundException("Patients can only update their own information");
        }

        // Set the current user's ID in the request
        request.setUserId(user.getUserID());

        // Update the patient disease information
        PatientDisease response = patientDiseaseService.updatePatientDisease(request);
        return ResponseEntity.ok(response);
    }

    // Get all diseases for a specific patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientDisease>> getDiseasesByPatientId(@PathVariable Long patientId, HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        // Allow only the patient's doctor or the patient to view this data
        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT") && !user.getUserID().equals(patientId)) {
            throw new UserNotFoundException("You can only view your own information");
        }

        List<PatientDisease> diseases = patientDiseaseService.getDiseasesByPatientId(patientId);
        return ResponseEntity.ok(diseases);
    }

    // Get all patients with a specific disease
    @GetMapping("/disease/{diseaseName}")
    public ResponseEntity<List<PatientDisease>> getPatientsWithDisease(@PathVariable String diseaseName, HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        // Allow only doctors to view the list of patients with a specific disease
        validateLoggedInDoctor(user);

        List<PatientDisease> patientsWithDisease = patientDiseaseService.getPatientsWithDisease(diseaseName);
        return ResponseEntity.ok(patientsWithDisease);
    }

    // Delete a patient's disease record (Patients can only delete their own records)
    @DeleteMapping("/{patientId}/{diseaseName}")
    public ResponseEntity<GeneralResponse> deletePatientDisease(@PathVariable Long patientId, @PathVariable String diseaseName, HttpServletRequest httpServletRequest) throws NotFoundException, UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        // If the user is a patient, ensure they are only deleting their own data
        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT") && !user.getUserID().equals(patientId)) {
            throw new UserNotFoundException("Patients can only delete their own information");
        }

        patientDiseaseService.deletePatientDisease(patientId, diseaseName);
        return ResponseEntity.ok(new GeneralResponse("Patient disease record deleted successfully"));
    }
}