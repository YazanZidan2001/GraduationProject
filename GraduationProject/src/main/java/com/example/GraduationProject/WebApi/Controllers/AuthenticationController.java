package com.example.GraduationProject.WebApi.Controllers;


import com.example.GraduationProject.Core.Services.PatientService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final PatientService patientService;
    private final LogoutService logoutService;
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> Login(@RequestBody @Valid LoginDTO request) throws UserNotFoundException {
        return ResponseEntity.ok(service.authenticate(request));
    }
    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }
    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) throws UserNotFoundException, MessagingException, MessagingException {
        service.sendVerificationCode(email);
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


}

