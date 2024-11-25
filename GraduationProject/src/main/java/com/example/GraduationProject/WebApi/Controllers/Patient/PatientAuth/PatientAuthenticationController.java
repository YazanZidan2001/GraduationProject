package com.example.GraduationProject.WebApi.Controllers.Patient.PatientAuth;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Doctor;
import com.example.GraduationProject.Core.Repositories.CategoryRepository;
import com.example.GraduationProject.Core.Repositories.SpecializationRepository;
import com.example.GraduationProject.Core.Services.DoctorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import com.example.GraduationProject.Common.Entities.Patient;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.PatientService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/patients/auth")
public class PatientAuthenticationController extends SessionManagement {

    private final PatientService patientService;
    private final AuthenticationService service;
    private final DoctorService doctorService;
    private final SpecializationRepository specializationRepository;
    private final CategoryRepository categoryRepository;

    @PutMapping("/{email}")
    public ResponseEntity<?> updatePatient(@RequestBody @Valid Patient request, @PathVariable String email, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatient(user);
        GeneralResponse d= patientService.updatePatient(request, email);
        return ResponseEntity.ok(d);

    }

    @PostMapping("/changePassword")
    public ResponseEntity<AuthenticationResponse> changePassword(@RequestParam String email,
                                                                 @RequestParam String oldPassword,
                                                                 @RequestParam String newPassword,
                                                                 HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatient(user);
        AuthenticationResponse response = service.ChangePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok(response);
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

    @GetMapping("getAllDoctors-by-specialization")
    public PaginationDTO<Doctor> getAllDoctors(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String search,
            @RequestParam(defaultValue = "", required = false) String specialization,
            HttpServletRequest httpServletRequest) throws UserNotFoundException {

        if (specialization != null && !specialization.isEmpty()) {
            boolean isValid = specializationRepository.existsBySpecialName(specialization);
            if (!isValid) {
                throw new IllegalArgumentException("Invalid specialization: " + specialization);
            }
        }

        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatient(user);

        return doctorService.getAllDoctorsBySpecialization(page, size, search, specialization);
    }

    @GetMapping("getAllDoctors-by-category")
    public PaginationDTO<Doctor> getAllDoctorsByCategory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String search,
            @RequestParam(defaultValue = "", required = false) String category,
            HttpServletRequest httpServletRequest) throws UserNotFoundException {

        // Validate the category if provided
        if (category != null && !category.isEmpty()) {
            boolean isValid = categoryRepository.existsByCategoryName(category);
            if (!isValid) {
                throw new IllegalArgumentException("Invalid category: " + category);
            }
        }

        // Extract and validate the logged-in user
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatient(user);

        // Call the service method to get doctors filtered by category
        return doctorService.getAllDoctorsByCategory(page, size, search, category);
    }


    @GetMapping("getAllDoctors")
    public PaginationDTO<Doctor> getAllDoctors(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "",required = false) String search ,
                                               HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInPatient(user);
        return doctorService.getAllDoctorsBySpecialization(page, size, search);
    }

}
