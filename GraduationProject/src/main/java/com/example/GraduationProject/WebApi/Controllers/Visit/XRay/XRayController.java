package com.example.GraduationProject.WebApi.Controllers.Visit.XRay;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.LabTest;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Entities.XRay;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.XRayService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@RequestMapping("x-rays")
@RequiredArgsConstructor
public class XRayController extends SessionManagement {

    private final XRayService xRayService;
    private final AuthenticationService authenticationService;

    /**
     * Add a new XRay (only doctors can add x-rays).
     */
    @PostMapping
    public ResponseEntity<String> addXRay(@RequestBody XRay xRay, HttpServletRequest request)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        xRayService.addXRay(xRay);
        return ResponseEntity.ok("XRay added successfully");
    }

    @PostMapping("/xray/batch")
    public ResponseEntity<String> addMultipleXRays(@RequestBody List<XRay> xRays, HttpServletRequest request)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        xRayService.addMultipleXRays(xRays);
        return ResponseEntity.status(HttpStatus.CREATED).body("Multiple XRays added successfully");
    }


    /**
     * NEW: Add multiple XRays *with* optional result files in a single request.
     * Accepts multipart/form-data with:
     *   - @RequestPart("xRays") => JSON array of XRay
     *   - @RequestPart("files") => list of MultipartFiles (image/PDF)
     */
    @PostMapping("/batch-with-files")
    public ResponseEntity<String> addMultipleXRaysWithFiles(
            @RequestPart("xRays") String xRaysJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            HttpServletRequest request
    ) throws UserNotFoundException, NotFoundException, IOException {

        // 1) Validate user (doctor, for example)
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // 2) Parse the JSON array from the string
        ObjectMapper objectMapper = new ObjectMapper();
        List<XRay> xRays = objectMapper.readValue(xRaysJson, new TypeReference<List<XRay>>() {});

        // 3) (Optional) If we require the same number of files as xRays, check
        if (files != null && !files.isEmpty() && files.size() != xRays.size()) {
            return ResponseEntity.badRequest().body("Number of files != number of XRay records.");
        }

        // 4) Call service
        xRayService.addMultipleXRaysWithFiles(xRays, files);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Multiple XRays with files added successfully");
    }


    /**
     * Get all x-rays for the logged-in patient.
     */
    @GetMapping("/patient/x-rays")
    public ResponseEntity<PaginationDTO<XRay>> getXRaysForLoggedInPatient(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        PaginationDTO<XRay> xRays = xRayService.getXRaysByPatientId(user.getUserID(), page, size);
        return ResponseEntity.ok(xRays);
    }

    /**
     * Get a specific x-ray for the logged-in patient by x-ray ID.
     */
    @GetMapping("/patient/x-rays/{xrayId}")
    public ResponseEntity<XRay> getXRayForLoggedInPatient(@PathVariable Long xrayId, HttpServletRequest request)
            throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        XRay xRay = xRayService.getXRayForPatientById(user.getUserID(), xrayId);
        return ResponseEntity.ok(xRay);
    }

    /**
     * Get all x-rays for a specific visit for the logged-in patient.
     */
    @GetMapping("/patient/visit/{visitId}")
    public ResponseEntity<PaginationDTO<XRay>> getXRaysForLoggedInPatientByVisit(
            @PathVariable Long visitId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        PaginationDTO<XRay> xRays = xRayService.getXRaysForPatientByVisitId(user.getUserID(), visitId, page, size);
        return ResponseEntity.ok(xRays);
    }

    /**
     * Get a specific x-ray for a specific visit for the logged-in patient.
     */
    @GetMapping("/patient/visit/{visitId}/{xrayId}")
    public ResponseEntity<XRay> getXRayForLoggedInPatientByVisitAndId(
            @PathVariable Long visitId,
            @PathVariable Long xrayId,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        XRay xRay = xRayService.getXRayForPatientByVisitAndId(user.getUserID(), visitId, xrayId);
        return ResponseEntity.ok(xRay);
    }

    /**
     * Get all x-rays for a specific patient (accessible by doctors).
     */
    @GetMapping("/doctor/patient/{patientId}")
    public ResponseEntity<PaginationDTO<XRay>> getXRaysForPatient(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        PaginationDTO<XRay> xRays = xRayService.getXRaysByPatientId(patientId, page, size);
        return ResponseEntity.ok(xRays);
    }

    /**
     * Get a specific x-ray for a specific patient (accessible by doctors).
     */
    @GetMapping("/doctor/patient/{patientId}/{xrayId}")
    public ResponseEntity<XRay> getXRayForPatientById(
            @PathVariable Long patientId,
            @PathVariable Long xrayId,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        XRay xRay = xRayService.getXRayForPatientById(patientId, xrayId);
        return ResponseEntity.ok(xRay);
    }

    /**
     * Get all x-rays for a specific visit for a specific patient (accessible by doctors).
     */
    @GetMapping("/doctor/patient/{patientId}/visit/{visitId}")
    public ResponseEntity<PaginationDTO<XRay>> getXRaysForPatientByVisit(
            @PathVariable Long patientId,
            @PathVariable Long visitId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        PaginationDTO<XRay> xRays = xRayService.getXRaysForPatientByVisitId(patientId, visitId, page, size);
        return ResponseEntity.ok(xRays);
    }

    /**
     * Get a specific x-ray for a specific visit for a specific patient (accessible by doctors).
     */
    @GetMapping("/doctor/patient/{patientId}/visit/{visitId}/{xrayId}")
    public ResponseEntity<XRay> getXRayForPatientByVisitAndId(
            @PathVariable Long patientId,
            @PathVariable Long visitId,
            @PathVariable Long xrayId,
            HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        XRay xRay = xRayService.getXRayForPatientByVisitAndId(patientId, visitId, xrayId);
        return ResponseEntity.ok(xRay);
    }



    @PostMapping("/{xrayId}/upload-result")
    public ResponseEntity<?> uploadXRayResult(
            @PathVariable Long xrayId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            XRay updatedXRay = xRayService.uploadXRayResultFile(xrayId, file);
            return ResponseEntity.ok("File uploaded successfully. Path: " + updatedXRay.getResultFilePath());
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error uploading file: " + ex.getMessage());
        }
    }

    @GetMapping("/{xrayId}/download-result")
    public ResponseEntity<Resource> downloadXRayResult(@PathVariable Long xrayId) throws IOException {
        XRay xray = xRayService.getXRayById(xrayId);
        String path = xray.getResultFilePath();
        if (path == null || path.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        File file = new File(path);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
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
