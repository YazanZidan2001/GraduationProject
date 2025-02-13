package com.example.GraduationProject.WebApi.Controllers.Visit.LabTest;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.LabTest;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.LabTestService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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


    @PostMapping("/batch-with-files")
    public ResponseEntity<?> addMultipleLabTestsWithFiles(
            @RequestPart("labTests") String labTestsJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            HttpServletRequest request
    ) {
        try {
            // 1) Validate user (Only doctors are allowed)
            String token = authenticationService.extractToken(request);
            User user = authenticationService.extractUserFromToken(token);
            validateLoggedInDoctor(user);

            // 2) Parse the JSON array from the string
            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            List<LabTest> labTests = mapper.readValue(labTestsJson, new TypeReference<List<LabTest>>() {});

            // 3) Validate the parsed LabTest objects
            if (labTests.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("LabTest list cannot be empty.");
            }
            for (LabTest labTest : labTests) {
                if (labTest.getVisitId() == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Each LabTest must have a visitId.");
                }
            }

            // 4) Call service
            labTestService.addMultipleLabTestsWithFiles(labTests, files);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Multiple Lab Tests added successfully with result files.");

        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found: " + ex.getMessage());
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + ex.getMessage());
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload error: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }
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


    @PostMapping("/{testId}/upload-result")
    public ResponseEntity<?> uploadLabTestResult(
            @PathVariable Long testId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            LabTest updatedLabTest = labTestService.uploadLabResultFile(testId, file);
            return ResponseEntity.ok("File uploaded successfully. Path: " + updatedLabTest.getResultFilePath());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error uploading file: " + ex.getMessage());
        }
    }

    @GetMapping("/{testId}/download-result")
    public ResponseEntity<Resource> downloadLabTestResult(@PathVariable Long testId) throws IOException, NotFoundException {
        LabTest labTest = labTestService.getLabTestById(testId);
        String path = labTest.getResultFilePath();

        if (path == null || path.isEmpty()) {
            throw new NotFoundException("Lab Test result not found for ID: " + testId);
        }

        File file = new File(path);
        if (!file.exists()) {
            throw new NotFoundException("Lab Test result file not found at the specified path.");
        }

        Resource resource = new UrlResource(file.toURI());
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }



}
