package com.example.GraduationProject.WebApi.Controllers.Visit.XRay;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Entities.XRay;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.XRayService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        xRayService.addXRay(xRay);
        return ResponseEntity.ok("XRay added successfully");
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
}
