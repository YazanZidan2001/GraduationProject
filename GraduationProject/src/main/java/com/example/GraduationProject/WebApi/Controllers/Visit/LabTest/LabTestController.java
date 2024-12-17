package com.example.GraduationProject.WebApi.Controllers.Visit.LabTest;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.LabTest;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.LabTestService;
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
@RequestMapping("lab-tests")
@RequiredArgsConstructor
public class LabTestController extends SessionManagement {

    private final LabTestService labTestService;
    private final AuthenticationService authenticationService;

    /**
     * Add a new lab test (only doctors can add lab tests).
     */
    @PostMapping
    public ResponseEntity<String> addLabTest(@RequestBody LabTest labTest, HttpServletRequest request)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        labTestService.addLabTest(labTest);
        return ResponseEntity.ok("Lab test added successfully");
    }

    @PostMapping("/labtest/batch")
    public ResponseEntity<String> addMultipleLabTests(@RequestBody List<LabTest> labTests, HttpServletRequest request)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        labTestService.addMultipleLabTests(labTests);
        return ResponseEntity.status(HttpStatus.CREATED).body("Multiple Lab Tests added successfully");
    }


    /**
     * Get all lab tests for the logged-in patient.
     */
    @GetMapping("/patient/lab-tests")
    public ResponseEntity<PaginationDTO<LabTest>> getLabTestsForLoggedInPatient(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        PaginationDTO<LabTest> labTests = labTestService.getLabTestsByPatientId(user.getUserID(), page, size);
        return ResponseEntity.ok(labTests);
    }

    /**
     * Get a specific lab test for the logged-in patient by lab test ID.
     */
    @GetMapping("/patient/lab-tests/{labTestId}")
    public ResponseEntity<LabTest> getLabTestForLoggedInPatient(@PathVariable Long labTestId, HttpServletRequest request)
            throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        LabTest labTest = labTestService.getLabTestForPatientById(user.getUserID(), labTestId);
        return ResponseEntity.ok(labTest);
    }


    /**
     * Get all lab tests for a specific visit for the logged-in patient.
     */
    @GetMapping("/patient/visit/{visitId}")
    public ResponseEntity<PaginationDTO<LabTest>> getLabTestsForLoggedInPatientByVisit(
            @PathVariable Long visitId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        PaginationDTO<LabTest> labTests = labTestService.getLabTestsForPatientByVisitId(user.getUserID(), visitId, page, size);
        return ResponseEntity.ok(labTests);
    }

    /**
     * Get a specific lab test for a specific visit for the logged-in patient.
     */
    @GetMapping("/patient/visit/{visitId}/{labTestId}")
    public ResponseEntity<LabTest> getLabTestForLoggedInPatientByVisitAndId(
            @PathVariable Long visitId,
            @PathVariable Long labTestId,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        LabTest labTest = labTestService.getLabTestForPatientByVisitAndId(user.getUserID(), visitId, labTestId);
        return ResponseEntity.ok(labTest);
    }

    /**
     * Get all lab tests for a specific patient (accessible by doctors).
     */
    @GetMapping("/doctor/patient/{patientId}")
    public ResponseEntity<PaginationDTO<LabTest>> getLabTestsForPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        PaginationDTO<LabTest> labTests = labTestService.getLabTestsByPatientId(patientId, page, size);
        return ResponseEntity.ok(labTests);
    }

    /**
     * Get a specific lab test for a specific patient (accessible by doctors).
     */
    @GetMapping("/doctor/patient/{patientId}/{labTestId}")
    public ResponseEntity<LabTest> getLabTestForPatientById(@PathVariable Long patientId, @PathVariable Long labTestId, HttpServletRequest request)
            throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        LabTest labTest = labTestService.getLabTestForPatientById(patientId, labTestId);
        return ResponseEntity.ok(labTest);
    }

    /**
     * Get all lab tests for a specific visit for a specific patient (accessible by doctors).
     */
    @GetMapping("/doctor/patient/{patientId}/visit/{visitId}")
    public ResponseEntity<PaginationDTO<LabTest>> getLabTestsForPatientByVisit(
            @PathVariable Long patientId,
            @PathVariable Long visitId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        PaginationDTO<LabTest> labTests = labTestService.getLabTestsForPatientByVisitId(patientId, visitId, page, size);
        return ResponseEntity.ok(labTests);
    }

    /**
     * Get a specific lab test for a specific visit for a specific patient (accessible by doctors).
     */
    @GetMapping("/doctor/patient/{patientId}/visit/{visitId}/{labTestId}")
    public ResponseEntity<LabTest> getLabTestForPatientByVisitAndId(
            @PathVariable Long patientId,
            @PathVariable Long visitId,
            @PathVariable Long labTestId,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        LabTest labTest = labTestService.getLabTestForPatientByVisitAndId(patientId, visitId, labTestId);
        return ResponseEntity.ok(labTest);
    }


}
