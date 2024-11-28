package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.*;
import com.example.GraduationProject.Common.Enums.Role;
import com.example.GraduationProject.Common.Enums.TokenType;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Repositories.*;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.WebApi.config.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final BloodTypeRepository bloodTypeRepository;
    private final DoctorRatingRepository doctorRatingRepository;


    @Transactional
    public DoctorRating addRating(Long doctorId, Integer ratingValue, String comments, User patient) throws NotFoundException {
        if (ratingValue < 1 || ratingValue > 5) {
            throw new IllegalArgumentException("Rating value must be between 1 and 5.");
        }

        // Build and save the rating
        DoctorRating rating = DoctorRating.builder()
                .doctorId(doctorId)
                .patientId(patient.getUserID())
                .ratingValue(ratingValue)
                .comments(comments)
                .build();

        return doctorRatingRepository.save(rating);
    }


    @Transactional
    public AuthenticationResponse addPatient(Patient request) throws UserNotFoundException {
        User user = request.getUser();

        // Check if a user with the same email already exists
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existingUser != null) {
            // Check if the existing user already has an associated patient
            if (existingUser.getPatient() != null) {
                throw new UserNotFoundException("User already has an associated patient");
            } else {
                // Reuse the existing user
                user = existingUser;
            }
        } else {
            // Manually set the UserID from the request (ensure the ID is provided in the JSON)
            user.setUserID(request.getPatientId());

            // Set role and encode password for the new user
            user.setRole(Role.PATIENT);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);

            // Save the new user
            user = userRepository.save(user);
        }

        // Create a new Patient instance
        Patient patient = Patient.builder()
                .patientId(user.getUserID())  // Set patientId to match UserID
                .user(user)                   // Associate with the created/reused user
                .bloodType(request.getBloodType()) // Directly use the BloodTypes enum
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .height(request.getHeight())
                .weight(request.getWeight())
                .remarks(request.getRemarks())
                .build();

        // Save the new patient
        patientRepository.save(patient);

        // Generate JWT tokens for the user
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .message("Patient added successfully")
                .build();
    }









    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    @Transactional
    public Optional<Patient> getPatient(String email) throws UserNotFoundException {
        Optional<Patient> patient = patientRepository.findByUserEmail(email);
        if (patient.isEmpty()) {
            throw new UserNotFoundException("Patient not found");
        }
        return patient;
    }

    @Transactional
    public GeneralResponse updatePatient(Patient request, String email) throws UserNotFoundException {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Patient not found"));
        User user = patient.getUser();
        user.setRole(Role.PATIENT);
        user.setFirstName(request.getUser().getFirstName());
        user.setLastName(request.getUser().getLastName());
        user.setPhone(request.getUser().getPhone());
        user.setEmail(request.getUser().getEmail());

        patient.setHeight(request.getHeight());
        patient.setWeight(request.getWeight());
        patient.setRemarks(request.getRemarks());

        patientRepository.save(patient);

        return GeneralResponse.builder().message("Patient updated successfully").build();
    }

    @Transactional
    public GeneralResponse deletePatient(String email) throws UserNotFoundException {
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Patient not found"));
        User user = patient.getUser();

        // Mark user as deleted
        user.setDeleted(true);
        userRepository.save(user);

        // Delete patient record
        patientRepository.delete(patient);

        return GeneralResponse.builder().message("Patient deleted successfully").build();
    }

    @Transactional
    public Page<Patient> getAllPatients(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return patientRepository.findAll(pageable);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserID());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    @Transactional
    public PaginationDTO<Patient> getAllPatients(int page, int size, String search) {
        // Normalize inputs
        if (search != null && search.isEmpty()) {
            search = null;
        }

        // Create a Pageable object
        Pageable pageable = PageRequest.of(page - 1, size);

        // Use the repository method with the search parameter
        Page<Patient> patientsPage = patientRepository.findAll(pageable, search);

        // Build and return PaginationDTO
        return PaginationDTO.<Patient>builder()
                .totalElements(patientsPage.getTotalElements())
                .totalPages(patientsPage.getTotalPages())
                .size(patientsPage.getSize())
                .number(patientsPage.getNumber() + 1) // Convert to 1-based index
                .numberOfElements(patientsPage.getNumberOfElements())
                .content(patientsPage.getContent())
                .build();
    }


}
