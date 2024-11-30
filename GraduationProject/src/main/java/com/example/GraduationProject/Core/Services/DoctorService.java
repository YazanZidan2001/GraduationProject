package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Core.Repositories.*;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Doctor;
import com.example.GraduationProject.Common.Entities.Specialization;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Entities.Token;
import com.example.GraduationProject.Common.Enums.Role;
import com.example.GraduationProject.Common.Enums.TokenType;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.WebApi.config.JwtService;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;  // Add this
    private final DoctorRatingRepository doctorRatingRepository;

    /**
     * Calculate and return the average rating for a specific doctor.
     *
     * @param doctorId The ID of the doctor.
     * @return The average rating or 0.0 if there are no ratings.
     */
    @Transactional(readOnly = true) // Ensure correct import
    public double getAverageRating(Long doctorId) {
        Double avgRating = doctorRatingRepository.calculateAverageRating(doctorId);
        return avgRating != null ? avgRating : 0.0;
    }

    @Transactional
    public AuthenticationResponse addDoctor(Doctor request) throws UserNotFoundException {
        User user = request.getUser();

        // Check if a user with the same email already exists
        User existingUser = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existingUser != null) {
            if (existingUser.getDoctor() != null) {
                throw new UserNotFoundException("User already has an associated doctor");
            } else {
                user = existingUser;
            }
        } else {
            user.setUserID(request.getDoctorId());
            user.setRole(Role.DOCTOR);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setActive(true);
            user = userRepository.save(user);
        }

        // Fetch specialization using the special_name
        Specialization specialization = specializationRepository.findById(request.getSpecial_name())
                .orElseThrow(() -> new UserNotFoundException("Specialization not found"));

        // Create Doctor object
        Doctor doctor = Doctor.builder()
                .doctorId(user.getUserID())
                .user(user)
                .specialization(specialization) // Set the fetched specialization
                .gender(request.getGender())
                .bio(request.getBio())
                .build();

        // Save Doctor
        doctorRepository.save(doctor);

        // Generate JWT tokens
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
    public Doctor findByUserID(long UserID) throws UserNotFoundException {
        Doctor doctor = doctorRepository.findByUserID(UserID)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found with UserID: " + UserID));
        return doctor;
    }

    @Transactional
    public PaginationDTO<Doctor> getAllDoctorsBySpecialization(int page, int size, String search, String specialization) {
        if (page < 1) {
            page = 1;
        }

        // Normalize inputs
        if (search != null && search.isEmpty()) {
            search = null;
        }
        if (specialization != null && specialization.isEmpty()) {
            specialization = null;
        }

        // Paginate results
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Doctor> doctors = doctorRepository.findAll_bySpecialization(pageable, search, specialization);

        // Build and return pagination DTO
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
    public PaginationDTO<Doctor> getAllDoctorsByCategory(int page, int size, String search, String category) {
        // Ensure the page number is at least 1
        if (page < 1) {
            page = 1;
        }

        // Normalize inputs: replace empty strings with null
        if (search != null && search.isEmpty()) {
            search = null;
        }
        if (category != null && category.isEmpty()) {
            category = null;
        }

        // Create pageable object with the given page and size
        Pageable pageable = PageRequest.of(page - 1, size);

        // Query the repository using the provided search and category
        Page<Doctor> doctors = doctorRepository.findAll_byCategory(pageable, search, category);

        // Build and return a PaginationDTO
        PaginationDTO<Doctor> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(doctors.getTotalElements());
        paginationDTO.setTotalPages(doctors.getTotalPages());
        paginationDTO.setSize(doctors.getSize());
        paginationDTO.setNumber(doctors.getNumber() + 1); // Adjust for 1-based page indexing
        paginationDTO.setNumberOfElements(doctors.getNumberOfElements());
        paginationDTO.setContent(doctors.getContent());

        return paginationDTO;
    }

    @Transactional
    public PaginationDTO<Doctor> getDoctorsByPatientDiseases(int page, int size, Long patientId, String search) {
        // Ensure the page number is at least 1
        if (page < 1) {
            page = 1;
        }

        // Normalize inputs: replace empty strings with null
        if (search != null && search.isEmpty()) {
            search = null;
        }

        // Create pageable object with the given page and size
        Pageable pageable = PageRequest.of(page - 1, size);

        // Query the repository using the provided search and patient ID
        Page<Doctor> doctors = doctorRepository.findDoctorsByPatientIdAndDiseases(pageable, patientId);

        // Build and return a PaginationDTO
        PaginationDTO<Doctor> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(doctors.getTotalElements());
        paginationDTO.setTotalPages(doctors.getTotalPages());
        paginationDTO.setSize(doctors.getSize());
        paginationDTO.setNumber(doctors.getNumber() + 1); // Adjust for 1-based page indexing
        paginationDTO.setNumberOfElements(doctors.getNumberOfElements());
        paginationDTO.setContent(doctors.getContent());

        return paginationDTO;
    }



    @Transactional
    public PaginationDTO<Doctor> getAllDoctorsBySpecialization(int page, int size, String search) {
        if (page < 1) {
            page = 1;
        }
        if (search != null && search.isEmpty()) {
            search = null;
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Doctor> doctors = doctorRepository.findAll_bySpecialization(pageable, search);
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
