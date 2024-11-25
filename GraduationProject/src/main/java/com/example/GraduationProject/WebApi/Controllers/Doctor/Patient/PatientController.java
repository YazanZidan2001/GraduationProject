package com.example.GraduationProject.WebApi.Controllers.Doctor.Patient;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Patient;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.PatientService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/doctors/patients")
public class PatientController extends SessionManagement {

    private final PatientService patientService;
    private final AuthenticationService service;

    @GetMapping
    public PaginationDTO<Patient> getPatients(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "", required = false) String search,
            HttpServletRequest request) throws UserNotFoundException {

        // Extract the logged-in user from the JWT token
        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);

        // Validate that the logged-in user is either a Doctor or an Admin
        validateLoggedInDoctorOrAdmin(user);

        // Fetch and return paginated patient data
        return patientService.getAllPatients(page, size, search);
    }

}
