package com.example.GraduationProject.WebApi.Controllers.Doctor.DoctorAuth;

import com.example.GraduationProject.Common.Entities.Patient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import com.example.GraduationProject.Common.Entities.Doctor;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.DoctorService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/doctors/auth")
public class DoctorAuthenticationController extends SessionManagement {

    private final DoctorService doctorService;
    private final AuthenticationService authenticationService;


    /**
     * Get the average rating for a specific doctor.
     * @param doctorId The ID of the doctor.
     * @return The average rating as a plain double.
     */
    @GetMapping("/average/{doctorId}")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long doctorId, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        // Call the service to calculate the average rating
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInAllUser(user);
        double avgRating = doctorService.getAverageRating(doctorId);

        // Return the average rating as a plain response
        return ResponseEntity.ok(avgRating);
    }

    @PutMapping("/{email}")
    public ResponseEntity<GeneralResponse> updateDoctor(@RequestBody @Valid Doctor request, @PathVariable String email, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Assuming updateDoctor handles updating both Doctor and associated User
        doctorService.updateDoctorByEmail(request, email);

        return ResponseEntity.ok(GeneralResponse.builder().message("Doctor updated successfully").build());
    }

    @PostMapping("/changePassword")
    public ResponseEntity<AuthenticationResponse> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpServletRequest httpServletRequest) throws UserNotFoundException {

        // Extract token and user
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        // Validate that the logged-in user exists
        validateLoggedInAllUser(user);

        // Call the service to change the password
        AuthenticationResponse response = authenticationService.changePasswordForToken(user, oldPassword, newPassword);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/me")
    public ResponseEntity<Doctor> getDoctorDetails(HttpServletRequest httpServletRequest) throws UserNotFoundException {
        // Extract token from the request
        String token = authenticationService.extractToken(httpServletRequest);

        // Get the patient details using the token
        Doctor doctor = doctorService.getDoctorDetailsFromToken(token);

        // Return the full patient details
        return ResponseEntity.ok(doctor);
    }

}
