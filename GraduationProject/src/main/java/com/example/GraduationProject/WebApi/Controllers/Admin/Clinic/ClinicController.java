package com.example.GraduationProject.WebApi.Controllers.Admin.Clinic;

import com.example.GraduationProject.WebApi.Exceptions.ClinicNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Clinic;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.ClinicService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/clinics")
@RequiredArgsConstructor
public class ClinicController extends SessionManagement {
    private final ClinicService clinicService;
    private final AuthenticationService service;

    @PostMapping("/")
    public ResponseEntity<GeneralResponse> addClinic(@RequestBody @Valid Clinic request, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        clinicService.addClinic(request);
        return ResponseEntity.ok(GeneralResponse.builder().message("Clinic added successfully").build());
    }

    @PutMapping("/{clinicId}")
    public ResponseEntity<GeneralResponse> updateClinic(@PathVariable Long clinicId, @RequestBody @Valid Clinic request, HttpServletRequest httpServletRequest) throws UserNotFoundException, ClinicNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        clinicService.updateClinic(request, clinicId);
        return ResponseEntity.ok(GeneralResponse.builder().message("Clinic updated successfully").build());
    }

    @GetMapping("/{clinicId}")
    public ResponseEntity<Clinic> getClinic(@PathVariable Long clinicId, HttpServletRequest httpServletRequest) throws UserNotFoundException, ClinicNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        Clinic clinic = clinicService.findClinicById(clinicId);
        return ResponseEntity.ok(clinic);
    }

    @GetMapping("")
    public PaginationDTO<Clinic> getAllClinics(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return clinicService.getAllClinics(page, size);
    }
}
