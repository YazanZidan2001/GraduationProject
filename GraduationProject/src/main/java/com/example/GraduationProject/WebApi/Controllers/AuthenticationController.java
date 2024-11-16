package com.example.GraduationProject.WebApi.Controllers;


import com.example.GraduationProject.Core.Services.PatientService;
import com.example.GraduationProject.Core.Services.VerificationService;
import com.example.GraduationProject.WebApi.config.JwtService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import com.example.GraduationProject.Common.Enums.Role;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.LoginDTO;
import com.example.GraduationProject.Common.Entities.Patient;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.LogoutService;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.GraduationProject.SessionManagement;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController extends SessionManagement {
    private final AuthenticationService service;
    private final PatientService patientService;
    private final LogoutService logoutService;
    private final VerificationService verificationService;

    private final JwtService jwtService;


    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> Login(@RequestBody @Valid LoginDTO request) throws UserNotFoundException {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/login-doctor")
    public ResponseEntity<GeneralResponse> login(@RequestBody @Valid LoginDTO request) throws UserNotFoundException {
        // Authenticate the user by contact info (email or phone) and password
        User user = service.getUserByContactInfo(request.getContactInfo()); // Checks if contactInfo is email or phone
        service.authenticateUserPassword(user, request.getPassword()); // Separate method to verify password

        // Prompt for verification method selection
        return ResponseEntity.ok(new GeneralResponse(
                "Login successful. Please choose a verification method: email or phone.",
                true
        ));
    }


    @PostMapping("/choose-verification-method")
    public ResponseEntity<GeneralResponse> chooseVerificationMethod(
            @RequestParam String contactInfo, // User's contact info (email or phone)
            @RequestParam String method // "email" or "phone"
    ) throws UserNotFoundException, IOException {
        // Retrieve the user by contact info (either email or phone)
        User user = service.getUserByContactInfo(contactInfo);

        // Set the preferred verification method and save it to the database
        user.setPreferred2faMethod(method);
        service.updateUser(user);

        // Send the verification code based on the chosen method
        verificationService.sendVerificationCode(user);

        return ResponseEntity.ok(new GeneralResponse("Verification code sent via " + method, true));
    }
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
    @PostMapping("/send-verification-code-to-resetPassword")
    public ResponseEntity<String> sendPasswordResetEmail(@RequestParam String email) throws UserNotFoundException, MessagingException, MessagingException {
        service.sendPasswordResetEmail(email);
        return ResponseEntity.ok("Verification code sent to email");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<GeneralResponse> verifyCodeAndResetPassword(@RequestParam String email,
                                                                      @RequestParam String verificationCode,
                                                                      @RequestBody String newPassword
    ) throws UserNotFoundException {
        GeneralResponse response = service.verifyCodeAndResetPassword(
                email, verificationCode, newPassword);
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<AuthenticationResponse> RegisterPatient(@RequestBody @Valid Patient request , HttpServletRequest httpServletRequest) throws UserNotFoundException {
        return new ResponseEntity<>(patientService.addPatient(request), HttpStatus.CREATED);
    }
    @GetMapping("/getUser")
    public User getUser(@RequestHeader("Authorization") String request) {
        String token = request.replace("Bearer ", "");
        return service.extractUserFromToken(token);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutService.logout(request, response, authentication);
        return ResponseEntity.status(HttpStatus.OK).body("Logged out successfully");
    }
    @PostMapping("/expire-token/{id}")
    public boolean expireToken(@PathVariable Long id,@RequestParam String token)  {

        return service.expiredToken(id,token);
    }
    @PostMapping("/changePassword")
    public ResponseEntity<AuthenticationResponse> changePassword(@RequestParam String email,
                                                                 @RequestParam String oldPassword,
                                                                 @RequestParam String newPassword,
                                                                 HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAllUser(user);
        AuthenticationResponse response = service.ChangePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok(response);
    }




    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(
            @RequestParam String contactInfo, // Either email or phone
            @RequestParam String method // "email" or "phone"
    ) throws UserNotFoundException, IOException {
        // Retrieve the user by contact info (either email or phone)
        User user = service.getUserByContactInfo(contactInfo);

        // Set the user's preferred method for sending the verification code
        user.setPreferred2faMethod(method);

        // Use the VerificationService to send the verification code
        verificationService.sendVerificationCode(user);

        return ResponseEntity.ok("Verification code sent via " + method);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<AuthenticationResponse> verifyCode(
            @RequestParam String contactInfo,
            @RequestParam String code
    ) throws UserNotFoundException {
        // Retrieve the user by contact info (either email or phone)
        User user = service.getUserByContactInfo(contactInfo);

        // Verify the provided code using the VerificationService
        boolean isCodeValid = verificationService.verifyCode(user, code);

        if (isCodeValid) {
            // Code is valid, generate access and refresh tokens
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            service.revokeAllUserTokens(user);
            service.saveUserToken(user, jwtToken);

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .role(user.getRole())
                    .message("Login completed successfully with two-factor authentication.")
                    .build());
        } else {
            // Code is invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationResponse.builder()
                            .message("Invalid or expired verification code")
                            .build());
        }
    }

}

