package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Core.Repositories.SpecializationRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Doctor;
import com.example.GraduationProject.Common.Entities.Specialization;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Entities.Token;
import com.example.GraduationProject.Common.Enums.Role;
import com.example.GraduationProject.Common.Enums.TokenType;
import com.example.GraduationProject.Core.Repositories.DoctorRepository;
import com.example.GraduationProject.Core.Repositories.UserRepository;
import com.example.GraduationProject.Core.Repositories.TokenRepository;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.WebApi.config.JwtService;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;  // Add this

    @Transactional
    public AuthenticationResponse addDoctor(Doctor request) throws UserNotFoundException {
        User user = request.getUser();

        // Check if a user with the same email already exists
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existingUser != null) {
            // Check if the existing user already has an associated doctor
            if (existingUser.getDoctor() != null) {
                throw new UserNotFoundException("User already has an associated doctor");
            } else {
                // Reuse the existing user
                user = existingUser;
            }
        } else {
            // Manually set the UserID from the request (ensure the ID is provided in the JSON)
            user.setUserID(request.getDoctorId());

            // Set role and encode password for the new user
            user.setRole(Role.DOCTOR);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Save the new user
            user = userRepository.save(user);
        }

        // Fetch the specialization based on the special_name
        Specialization specialization = specializationRepository.findById(request.getSpecialization().getSpecial_name())
                .orElseThrow(() -> new RuntimeException("Specialization not found"));

        // Create a new Doctor instance
        Doctor doctor = Doctor.builder()
                .doctorId(user.getUserID())        // Set doctorId to match UserID
                .user(user)                        // Associate with the created/reused user
                .specialization(specialization)    // Use the fetched specialization
                .gender(request.getGender())
                .bio(request.getBio())
                .build();

        // Save the new doctor
        doctorRepository.save(doctor);

        // Generate JWT tokens for the user
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .message("Doctor added successfully")
                .build();
    }






    @Transactional
    public void updateDoctor(Doctor request, Long doctorId) throws UserNotFoundException {
        var doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found"));

        var user = doctor.getUser();
        user.setRole(Role.DOCTOR);
        user.setFirstName(request.getUser().getFirstName());
        user.setLastName(request.getUser().getLastName());
        user.setDateOfBirth(request.getUser().getDateOfBirth());
        user.setPhone(request.getUser().getPhone());
        user.setEmail(request.getUser().getEmail());
        user.setPassword(passwordEncoder.encode(request.getUser().getPassword()));

        userRepository.save(user);

        doctor.setSpecialization(request.getSpecialization());
        doctorRepository.save(doctor);
    }

    @Transactional
    public void updateDoctorByEmail(Doctor request, String email) throws UserNotFoundException {
        var doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found"));

        var user = doctor.getUser();
        user.setRole(Role.DOCTOR);
        user.setFirstName(request.getUser().getFirstName());
        user.setLastName(request.getUser().getLastName());
        user.setDateOfBirth(request.getUser().getDateOfBirth());
        user.setPhone(request.getUser().getPhone());
        user.setEmail(request.getUser().getEmail());
        user.setPassword(passwordEncoder.encode(request.getUser().getPassword()));

        userRepository.save(user);

        doctor.setSpecialization(request.getSpecialization());
        doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor findDoctorByEmail(String email) throws UserNotFoundException {
        Doctor doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found with email: " + email));
        return doctor;
    }

    @Transactional
    public PaginationDTO<Doctor> getAllDoctors(int page, int size, String search, String specialization) {
        if (page < 1) {
            page = 1;
        }
        if (search != null && search.isEmpty()) {
            search = null;
        }
        if (specialization != null && specialization.isEmpty()) {
            specialization = null;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Specialization spec = (specialization != null) ? Specialization.builder().special_name(specialization).build() : null;
        Page<Doctor> doctors = doctorRepository.findAll(pageable, search, specialization);
        PaginationDTO<Doctor> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(doctors.getTotalElements());
        paginationDTO.setTotalPages(doctors.getTotalPages());
        paginationDTO.setSize(doctors.getSize());
        paginationDTO.setNumber(doctors.getNumber() + 1);
        paginationDTO.setNumberOfElements(doctors.getNumberOfElements());
        paginationDTO.setContent(doctors.getContent());
        return paginationDTO;
    }

    @Transactional
    public PaginationDTO<Doctor> getAllDoctors(int page, int size, String search) {
        if (page < 1) {
            page = 1;
        }
        if (search != null && search.isEmpty()) {
            search = null;
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Doctor> doctors = doctorRepository.findAll(pageable, search);
        PaginationDTO<Doctor> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(doctors.getTotalElements());
        paginationDTO.setTotalPages(doctors.getTotalPages());
        paginationDTO.setSize(doctors.getSize());
        paginationDTO.setNumber(doctors.getNumber() + 1);
        paginationDTO.setNumberOfElements(doctors.getNumberOfElements());
        paginationDTO.setContent(doctors.getContent());
        return paginationDTO;
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
