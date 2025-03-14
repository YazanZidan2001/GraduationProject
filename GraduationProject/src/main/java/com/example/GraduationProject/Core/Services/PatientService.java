package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.*;
import com.example.GraduationProject.Common.Entities.Patient;
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
    public Patient getPatientDetailsFromToken(String token) throws UserNotFoundException {
        // Extract user details from the token
        String email = jwtService.extractUsername(token); // Assuming the email is stored in the token

        // Find the patient by the email associated with the user
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Patient not found"));

        // Return the full patient object with all relevant details
        return patient;
    }

    @Transactional
    public GeneralResponse updatePatientDetailsFromToken(String token, Patient updatedPatientDetails) throws UserNotFoundException {
        // Extract the user (patient) from the token
        String email = jwtService.extractUsername(token);

        // Retrieve the patient using the email from the token
        Patient patient = patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Patient not found with email: " + email));

        // Update the patient's details based on the provided data
        User user = patient.getUser();

        // Update user's details
        if (updatedPatientDetails.getUser().getFirstName() != null) {
            user.setFirstName(updatedPatientDetails.getUser().getFirstName());
        }
        if (updatedPatientDetails.getUser().getLastName() != null) {
            user.setLastName(updatedPatientDetails.getUser().getLastName());
        }
        if (updatedPatientDetails.getUser().getPhone() != null) {
            user.setPhone(updatedPatientDetails.getUser().getPhone());
        }
        if (updatedPatientDetails.getUser().getEmail() != null) {
            user.setEmail(updatedPatientDetails.getUser().getEmail());
        }
        if (updatedPatientDetails.getUser().getDateOfBirth() != null) {
            user.setDateOfBirth(updatedPatientDetails.getUser().getDateOfBirth());
        }

        // Update the patient's details
        if (updatedPatientDetails.getGender() != null) {
            patient.setGender(updatedPatientDetails.getGender());
        }
        if (updatedPatientDetails.getHeight() != null) {
            patient.setHeight(updatedPatientDetails.getHeight());
        }
        if (updatedPatientDetails.getWeight() != null) {
            patient.setWeight(updatedPatientDetails.getWeight());
        }
        if (updatedPatientDetails.getRemarks() != null) {
            patient.setRemarks(updatedPatientDetails.getRemarks());
        }
        if (updatedPatientDetails.getBloodType() != null) {
            patient.setBloodType(updatedPatientDetails.getBloodType());
        }

        // Save the updated patient and user
        userRepository.save(user);
        patientRepository.save(patient);

        // Return a success message
        return GeneralResponse.builder().message("Patient details updated successfully").build();
    }

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
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        // Check if a user with the same phone number already exists
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("A user with this phone number already exists.");
        }

        // Check if a user with the same ID already exists
        if (userRepository.existsById(request.getPatientId())) {
            throw new IllegalArgumentException("A user with this ID already exists.");
        }

        // Create and save new user
        user.setUserID(request.getPatientId());
        user.setRole(Role.PATIENT);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user = userRepository.save(user);

        // Create and save new patient
        Patient patient = Patient.builder()
                .patientId(user.getUserID())  // Set patientId to match UserID
                .user(user)                   // Associate with the created/reused user
                .bloodType(request.getBloodType()) // Directly use the BloodTypes enum
                .gender(request.getGender())
                .height(request.getHeight())
                .weight(request.getWeight())
                .remarks(request.getRemarks())
                .build();

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
    public Patient findByPatientId(long UserID) throws UserNotFoundException {
        Patient patient = patientRepository.findByPatientId(UserID)
                .orElseThrow(() -> new UserNotFoundException("Patient not found with UserID: " + UserID));
        return patient;

//        Patient patient = patientRepository.findByPatientId(UserID);

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
