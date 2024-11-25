package com.example.GraduationProject.WebApi.Controllers.Admin.Doctors;

import com.example.GraduationProject.Common.Entities.Category;
import com.example.GraduationProject.Core.Repositories.CategoryRepository;
import com.example.GraduationProject.Core.Repositories.SpecializationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Doctor;
import com.example.GraduationProject.Common.Entities.User;
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
    private final SpecializationRepository specializationRepository;  // Add this
    private final CategoryRepository categoryRepository;

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
        validateLoggedInAdmin(user);

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
        validateLoggedInAdmin(user);

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
        validateLoggedInAdmin(user);
        return doctorService.getAllDoctorsBySpecialization(page, size, search);
    }


}
