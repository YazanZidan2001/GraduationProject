package com.example.GraduationProject.WebApi.Controllers.Admin.Doctors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Doctor;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Entities.Specialization;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.DoctorService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/doctors")
@RequiredArgsConstructor
public class DoctorController extends SessionManagement {
    private final DoctorService doctorService;
    private final AuthenticationService service;
    @PostMapping("/")
    public ResponseEntity<AuthenticationResponse> addDoctor(
            @RequestBody @Valid Doctor request,
            HttpServletRequest httpServletRequest) throws Exception {

        // Extract and validate the user from the token
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        // Check if the specialization is provided
        if (request.getSpecialization() == null || request.getSpecialization().getSpecial_name() == null) {
            throw new IllegalArgumentException("Specialization must be provided");
        }

        return ResponseEntity.ok(doctorService.addDoctor(request));
    }


    @PutMapping ("/{doctorId}")
    public ResponseEntity<GeneralResponse> updateDoctor(@PathVariable Long doctorId,  @RequestBody @Valid Doctor request, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        doctorService.updateDoctor(request,  doctorId);
        return ResponseEntity.ok(GeneralResponse.builder().message("Doctor updated successfully").build());
    }

    @GetMapping("/{email}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable String email, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        Doctor doctor = doctorService.findDoctorByEmail(email);
        // System.out.println(doctor);
        return ResponseEntity.ok(doctor);
    }

    @GetMapping("")
    public PaginationDTO<Doctor> getAllDoctors(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "",required = false) String search ,
                                               @RequestParam(defaultValue = "",required = false) Specialization specialization,
                                               HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return doctorService.getAllDoctors(page, size, search, String.valueOf(specialization));
    }


}
