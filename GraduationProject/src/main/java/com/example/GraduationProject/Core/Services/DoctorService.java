package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.*;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Repositories.*;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
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
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("A user with this email already exists.");
        }

        // Check if a user with the same phone number already exists
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("A user with this phone number already exists.");
        }

        // Check if a user with the same ID already exists
        if (userRepository.existsById(request.getDoctorId())) {
            throw new IllegalArgumentException("A user with this ID already exists.");
        }

        // Create and save new user
        user.setUserID(request.getDoctorId());
        user.setRole(Role.DOCTOR);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        user = userRepository.save(user);

        // Fetch specialization using the special_name
        Specialization specialization = specializationRepository.findById(request.getSpecial_name())
                .orElseThrow(() -> new UserNotFoundException("Specialization not found"));

        // Create and save new doctor
        Doctor doctor = Doctor.builder()
                .doctorId(user.getUserID())
                .user(user)
                .specialization(specialization)
                .gender(request.getGender())
                .bio(request.getBio())
                .build();

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
    public GeneralResponse updateDoctorProfile(Long doctorId, Doctor request) throws UserNotFoundException {
        // البحث عن الطبيب بواسطة ID
        var doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found"));

        // الحصول على بيانات المستخدم المرتبطة بالطبيب
        var user = doctor.getUser();

        // تحديث معلومات المستخدم
        if (request.getUser().getFirstName() != null) {
            user.setFirstName(request.getUser().getFirstName());
        }
        if (request.getUser().getLastName() != null) {
            user.setLastName(request.getUser().getLastName());
        }
        if (request.getUser().getPhone() != null) {
            user.setPhone(request.getUser().getPhone());
        }
        if (request.getUser().getDateOfBirth() != null) {
            user.setDateOfBirth(request.getUser().getDateOfBirth());
        }

        // تحديث معلومات الطبيب
        if (request.getBio() != null) {
            doctor.setBio(request.getBio());
        }
        if (request.getSpecialization() != null) {
            doctor.setSpecialization(request.getSpecialization());
        }

        // حفظ التعديلات في قاعدة البيانات
        userRepository.save(user);
        doctorRepository.save(doctor);

        return GeneralResponse.builder()
                .message("Doctor profile updated successfully")
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


    @Transactional
    public Doctor getDoctorDetailsFromToken(String token) throws UserNotFoundException {
        // Extract user details from the token
        String email = jwtService.extractUsername(token); // Assuming the email is stored in the token

        // Find the patient by the email associated with the user
        Doctor doctor = doctorRepository.findByDoctorEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Doctor not found"));

        // Return the full patient object with all relevant details
        return doctor;
    }
}
