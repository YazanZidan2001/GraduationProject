package com.example.GraduationProject.WebApi.Controllers.Patient.PatientAuth;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.*;
import com.example.GraduationProject.Core.Repositories.CategoryRepository;
import com.example.GraduationProject.Core.Repositories.SpecializationRepository;
import com.example.GraduationProject.Core.Services.DoctorService;
import com.example.GraduationProject.Core.Services.PatientLikeDoctorService;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.PatientService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/patients/auth")
public class PatientAuthenticationController extends SessionManagement {

    private final PatientService patientService;
    private final AuthenticationService authenticationService;
    private final DoctorService doctorService;
    private final SpecializationRepository specializationRepository;
    private final CategoryRepository categoryRepository;
    private final PatientLikeDoctorService patientLikeDoctorService;

    @PostMapping("/rating/{doctorId}")
    public ResponseEntity<GeneralResponse> addRating(
            @PathVariable Long doctorId,
            @RequestParam Integer ratingValue,
            @RequestParam(required = false) String comments,
            HttpServletRequest httpServletRequest)  {

        try {
            // Extract patient from token
            String token = authenticationService.extractToken(httpServletRequest);
            User user = authenticationService.extractUserFromToken(token);
            validateLoggedInPatient(user);
            User patient = authenticationService.extractUserFromToken(token);

            // Call service to add the rating
            patientService.addRating(doctorId, ratingValue, comments, patient);

            // Return success response
            return ResponseEntity.ok(GeneralResponse.builder()
                    .message("Rating added successfully.")
                    .success(true)
                    .build());
        } catch (IllegalArgumentException e) {
            // Handle validation error
            return ResponseEntity.badRequest().body(GeneralResponse.builder()
                    .message("Failed to add rating.")
                    .success(false)
                    .errors(List.of(e.getMessage()))
                    .build());
        } catch (NotFoundException | UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @PostMapping("/liked_doctor/{doctorId}")
    public ResponseEntity<GeneralResponse> likeDoctor(@PathVariable Long doctorId,
                                                      @RequestParam boolean isLike,
                                                      HttpServletRequest httpServletRequest) throws UserNotFoundException {
        // Extract patient from token
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        // Use the patient's ID to like/dislike a doctor
        Long patientId = user.getUserID();
        patientLikeDoctorService.likeDoctor(patientId, doctorId, isLike);

        String message = isLike ? "Doctor liked successfully" : "Doctor disliked successfully";
        return ResponseEntity.ok(GeneralResponse.builder().message(message).build());
    }

    @GetMapping("/liked_doctors")
    public ResponseEntity<List<patientLikeDoctor>> getLikedDoctors(HttpServletRequest httpServletRequest) throws UserNotFoundException {
        // Extract patient from token
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        // Use the patient's ID to fetch liked doctors
        Long patientId = user.getUserID();
        List<patientLikeDoctor> likedDoctors = patientLikeDoctorService.getAllLikedDoctorsByPatientId(patientId);

        return ResponseEntity.ok(likedDoctors);
    }


    @PutMapping("/{email}")
    public ResponseEntity<?> updatePatient(@RequestBody @Valid Patient request, @PathVariable String email, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);
        GeneralResponse d= patientService.updatePatient(request, email);
        return ResponseEntity.ok(d);

    }

    @PostMapping("/changePassword")
    public ResponseEntity<AuthenticationResponse> changePassword(@RequestParam String email,
                                                                 @RequestParam String oldPassword,
                                                                 @RequestParam String newPassword,
                                                                 HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);
        AuthenticationResponse response = authenticationService.ChangePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{email}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable String email, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
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

        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
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
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);

        // Call the service method to get doctors filtered by category
        return doctorService.getAllDoctorsByCategory(page, size, search, category);
    }

    @GetMapping("getDoctors-by-patient-diseases")
    public PaginationDTO<Doctor> getDoctorsByPatientDiseases(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String search,
            HttpServletRequest httpServletRequest) throws UserNotFoundException {

        // Extract and validate the logged-in user
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);


        // Get the patient's ID
        Long patientId = user.getPatient().getPatientId();

        // Call the service method to get doctors filtered by patient diseases
        return doctorService.getDoctorsByPatientDiseases(page, size, patientId, search);
    }



    @GetMapping("getAllDoctors")
    public PaginationDTO<Doctor> getAllDoctors(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "",required = false) String search ,
                                               HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInPatient(user);
        return doctorService.getAllDoctorsBySpecialization(page, size, search);
    }

}
