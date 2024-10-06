package com.example.GraduationProject.WebApi.Controllers.Admin.Clinic;

import com.example.GraduationProject.Common.Entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.DoctorClinic;
import com.example.GraduationProject.Core.Services.DoctorClinicService;
import com.example.GraduationProject.WebApi.Exceptions.DoctorClinicNotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/doctor-clinics")
@RequiredArgsConstructor
public class DoctorClinicController extends SessionManagement {
    private final DoctorClinicService doctorClinicService;
    private final AuthenticationService service;

    @PostMapping("/")
    public ResponseEntity<GeneralResponse> addDoctorClinic(@RequestBody @Valid DoctorClinic request, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        doctorClinicService.addDoctorClinic(request);
        return ResponseEntity.ok(GeneralResponse.builder().message("DoctorClinic added successfully").build());
    }

    @PutMapping("/{doctorId}/{clinicId}")
    public ResponseEntity<GeneralResponse> updateDoctorClinic(@PathVariable Long doctorId, @PathVariable Long clinicId, @RequestBody @Valid DoctorClinic request, HttpServletRequest httpServletRequest) throws DoctorClinicNotFoundException, UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        doctorClinicService.updateDoctorClinic(request, doctorId, clinicId);
        return ResponseEntity.ok(GeneralResponse.builder().message("DoctorClinic updated successfully").build());
    }

    @GetMapping("/{doctorId}/{clinicId}")
    public ResponseEntity<DoctorClinic> getDoctorClinic(@PathVariable Long doctorId, @PathVariable Long clinicId, HttpServletRequest httpServletRequest) throws DoctorClinicNotFoundException, UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        DoctorClinic doctorClinic = doctorClinicService.findDoctorClinic(doctorId, clinicId);
        return ResponseEntity.ok(doctorClinic);
    }

    @GetMapping("")
    public PaginationDTO<DoctorClinic> getAllDoctorClinics(@RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        return doctorClinicService.getAllDoctorClinics(page, size);
    }
}
