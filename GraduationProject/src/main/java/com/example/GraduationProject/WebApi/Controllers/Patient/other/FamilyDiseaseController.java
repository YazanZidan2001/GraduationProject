package com.example.GraduationProject.WebApi.Controllers.Patient.other;

import com.example.GraduationProject.Common.Entities.FamilyDisease;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.FamilyDiseaseService;
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
import java.util.Optional;

@RestController
@RequestMapping("/patients/family-diseases")
@RequiredArgsConstructor
public class FamilyDiseaseController extends SessionManagement {

    private final FamilyDiseaseService familyDiseaseService;
    private final AuthenticationService authenticationService;

    // Add a family disease record
    @PostMapping("/")
    public ResponseEntity<FamilyDisease> addFamilyDisease(@RequestBody @Valid FamilyDisease request, HttpServletRequest httpServletRequest)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT")
                && !user.getUserID().equals(request.getPatient().getPatientId())) {
            throw new UserNotFoundException("Patients can only add their own family disease records");
        }

        FamilyDisease response = familyDiseaseService.addFamilyDisease(request);
        return ResponseEntity.ok(response);
    }

    // Update a family disease record
    @PutMapping("/update")
    public ResponseEntity<FamilyDisease> updateFamilyDisease(@RequestBody @Valid FamilyDisease request, HttpServletRequest httpServletRequest)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT")
                && !user.getUserID().equals(request.getPatient().getPatientId())) {
            throw new UserNotFoundException("Patients can only update their own family disease records");
        }

        FamilyDisease response = familyDiseaseService.updateFamilyDisease(request);
        return ResponseEntity.ok(response);
    }

    // Get all family diseases for a specific patient
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<FamilyDisease>> getFamilyDiseasesByPatientId(@PathVariable Long patientId, HttpServletRequest httpServletRequest)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT") && !user.getUserID().equals(patientId)) {
            throw new UserNotFoundException("Patients can only view their own family disease records");
        }

        List<FamilyDisease> diseases = familyDiseaseService.getFamilyDiseasesByPatientId(patientId);
        return ResponseEntity.ok(diseases);
    }

    // Delete a family disease record
    @DeleteMapping("/{familyDiseaseId}")
    public ResponseEntity<GeneralResponse> deleteFamilyDisease(@PathVariable Long familyDiseaseId, HttpServletRequest httpServletRequest)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);

        familyDiseaseService.deleteFamilyDisease(familyDiseaseId);
        return ResponseEntity.ok(new GeneralResponse("Family disease record deleted successfully"));
    }

    // Get family diseases by family member
    @GetMapping("/family-member/{familyMember}")
    public ResponseEntity<List<FamilyDisease>> getFamilyDiseasesByFamilyMember(@PathVariable String familyMember, HttpServletRequest httpServletRequest) throws NotFoundException, UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);

        List<FamilyDisease> familyDiseases = familyDiseaseService.getFamilyDiseasesByFamilyMember(familyMember);
        return ResponseEntity.ok(familyDiseases);
    }



    @GetMapping("/patient/{patientId}/family-member/{familyMember}")
    public ResponseEntity<Optional<FamilyDisease>> getFamilyDiseasesByPatientAndFamilyMember(
            @PathVariable Long patientId,
            @PathVariable String familyMember,
            HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT") && !user.getUserID().equals(patientId)) {
            throw new UserNotFoundException("Patients can only view their own family disease records");
        }

        Optional<FamilyDisease> familyDiseases = familyDiseaseService.getFamilyDiseasesByPatientAndFamilyMember(patientId, familyMember);
        return ResponseEntity.ok(familyDiseases);
    }

}
