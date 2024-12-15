package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.SessionManagement;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;
import com.example.GraduationProject.Common.DTOs.LoginDTO;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Email;
import com.example.GraduationProject.Common.Entities.Token;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Enums.Role;
import com.example.GraduationProject.Common.Enums.TokenType;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Repositories.EmailRepository;
import com.example.GraduationProject.Core.Repositories.TokenRepository;
import com.example.GraduationProject.Core.Repositories.UserRepository;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.WebApi.config.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


@Service
@RequiredArgsConstructor
public class AuthenticationService extends SessionManagement {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final EmailRepository emailRepository;


    @Transactional
    public GeneralResponse uploadPhoto(String token, MultipartFile file) throws UserNotFoundException, IOException {
        User user = extractUserFromToken(token);
        validateLoggedInAllUser(user);

        // Directory setup
        String folderPath = "user-photos/";
        File folder = new File(folderPath);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create directory for photos.");
        }

        // Validate file format and size
        String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if (!List.of("jpg", "jpeg", "png").contains(fileExtension)) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, JPEG, and PNG are allowed.");
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB limit
            throw new IllegalArgumentException("File size exceeds the 5MB limit.");
        }

        // Generate a unique file name
        String fileName = "user_" + user.getUserID() + "_" + System.currentTimeMillis() + "." + fileExtension;
        File photoFile = new File(folderPath + fileName);

        // Save the file locally
        file.transferTo(photoFile);

        // Update the user's photo path
        user.setPhotoPath(folderPath + fileName);
        repository.save(user);

        return GeneralResponse.builder()
                .message("Photo uploaded successfully")
                .build();
    }


    @Transactional
    public Resource getPhotoForUser(User user) throws IOException {
        String photoPath = user.getPhotoPath();
        if (photoPath == null || photoPath.isEmpty()) {
            throw new FileNotFoundException("No photo found for the user.");
        }

        File photoFile = new File(photoPath);
        if (!photoFile.exists()) {
            throw new FileNotFoundException("Photo file not found at path: " + photoPath);
        }

        // Return the photo as a resource
        return new UrlResource(photoFile.toURI());
    }






    public User getUserByContactInfo(String contactInfo) throws UserNotFoundException {
        // Check if contactInfo matches email format
        if (contactInfo.contains("@")) {
            return repository.findByEmail(contactInfo)
                    .orElseThrow(() -> new UserNotFoundException("User not found with provided email: " + contactInfo));
        } else {
            // Assume contactInfo is a phone number
            return repository.findByPhone(contactInfo)
                    .orElseThrow(() -> new UserNotFoundException("User not found with provided phone number: " + contactInfo));
        }
    }

    public Long getUserIdByToken(String token) throws UserNotFoundException {
        // Find the token by its value
        Token tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new UserNotFoundException("Token not found"));

        // Return the associated user's ID
        return tokenEntity.getUser().getUserID();
    }

    @Transactional
    public AuthenticationResponse addUser(Long userId, User user) throws UserNotFoundException, IOException {
        // Check if the UserID already exists
        if (repository.existsById(userId)) {
            throw new UserNotFoundException("UserID already exists."); // Custom exception
        }

        // Set the UserID manually
        user.setUserID(userId);

        // Validate email format or other fields as needed
//        validateUser(user);

        // Encode the password and set deletion status
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDeleted(false);
        user.setActive(true);

        // Save user with manually assigned UserID
        var savedUser = repository.save(user);

        // Generate tokens
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);

        // Build and return the authentication response
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .message("User " + user.getRole() + " added successfully")
                .build();
    }

    public void updateUser(User user) {
        repository.save(user);
    }

    @Transactional
    public GeneralResponse UpdateUser(User userRequest, Long id) throws UserNotFoundException {
        var user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        repository.save(user);
        return GeneralResponse.builder()
                .message("User updated successfully")
                .build();
    }

    @Transactional
    public GeneralResponse DeleteUser(Long id) throws UserNotFoundException {
        var user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setDeleted(true);
        repository.save(user);
        return GeneralResponse.builder()
                .message("User deleted successfully")
                .build();
    }

    @Transactional
    public User GetUser(Long id) throws UserNotFoundException {
        var user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.isDeleted()) {
            throw new UserNotFoundException("User not found");
        }

        return user;
    }

    @Transactional
    public PaginationDTO<User> GetAllUsers(int page, int size, String search, Role role) {

        if(search != null && search.isEmpty()){
            search = null;
        }
        if(role != null && !EnumSet.allOf(Role.class).contains(role)){
            role = null;
        }
        if (page < 1) {
            page = 1;
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<User> userPage = repository.findAll(pageRequest, search, role);

        PaginationDTO<User> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(userPage.getTotalElements());
        paginationDTO.setTotalPages(userPage.getTotalPages());
        paginationDTO.setSize(userPage.getSize());
        paginationDTO.setNumber(userPage.getNumber() + 1);
        paginationDTO.setNumberOfElements(userPage.getNumberOfElements());
        paginationDTO.setContent(userPage.getContent());

        return paginationDTO;
    }

    @Transactional
    public Page<User> getAllUsersByRole(Role role, int page, int size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        return repository.findAllByRole(role, pageable);
    }

    public void authenticateUserPassword(User user, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), password)
        );
    }

    @Transactional
    public AuthenticationResponse authenticate(LoginDTO request) throws UserNotFoundException {
        // Determine if contactInfo is an email or phone
        User user;
        if (request.getContactInfo().contains("@")) {
            // Treat as email
            user = repository.findByEmail(request.getContactInfo())
                    .orElseThrow(() -> new UserNotFoundException("User not found with provided email"));
        } else {
            // Treat as phone number
            user = repository.findByPhone(request.getContactInfo())
                    .orElseThrow(() -> new UserNotFoundException("User not found with provided phone number"));
        }

        // Authenticate the user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(), // Use user's email for authentication
                        request.getPassword()
                )
        );

        // Generate tokens
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .role(user.getRole())
                .message("User LoggedIn successfully")
                .build();
    }


    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }


    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserID());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
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
            var user = this.repository.findByEmail(userEmail)
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

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }


    public User extractUserFromToken(String token) {
        String username = jwtService.extractUsername(token);
        return repository.findByEmail(username).orElse(null);
    }


    @Transactional
    public GeneralResponse resetPassword(String email, String password) throws UserNotFoundException {
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(password));
        repository.save(user);
//        var jwtToken = jwtService.generateToken(user);
//        var refreshToken = jwtService.generateRefreshToken(user);
//        saveUserToken(savedUser, jwtToken);

        return GeneralResponse.builder()
                .message("Password reset successfully")
                .build();
    }

    @Transactional
    public void sendPasswordResetEmail(String email) throws UserNotFoundException, MessagingException {
        var userEmail = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String verificationCode = UUID.randomUUID().toString();
        Email emailEntity = Email.builder()
                .email(email)
                .verificationCode(verificationCode)
                .verified(false)
                .build();
        emailRepository.save(emailEntity);
        String verificationUrl = "http://localhost:8080/resetPasswordPage?verificationCode=" + verificationCode + "&email=" + email;
        emailService.sendPasswordResetEmail(email, "Email Verification", verificationUrl);
    }

    @Transactional
    public GeneralResponse verifyCodeAndResetPassword(String email, String verificationCode, String newPassword) throws UserNotFoundException {
        Email emailEntity = emailRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("email not found"));
        if (emailEntity.getVerificationCode().equals(verificationCode)) {
            emailEntity.setVerified(true);
            emailRepository.save(emailEntity);
        } else {
            throw new UserNotFoundException("Invalid verification code ");
        }
        return resetPassword(email, newPassword);
    }


    @Transactional
    public AuthenticationResponse ChangePassword(String email, String oldPassword, String newPassword) throws UserNotFoundException {
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            var savedUser = repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            saveUserToken(savedUser, jwtToken);
            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .message("Password changed successfully")
                    .build();
        } else {
            throw new UserNotFoundException("Invalid old password");
        }
    }


    @Transactional
    public boolean expiredToken(Long id, String token)  {
        boolean userToken = tokenRepository.findValidTokenByUserAndToken(id, token).isPresent();

        if(userToken){
            return false;
        }
        return true;
    }

}
