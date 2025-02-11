package com.example.GraduationProject.WebApi.Controllers.Doctor.Patient;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Patient;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Responses.AuthenticationResponse;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.PatientService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/addPatient")
    public ResponseEntity<?> addPatient(@RequestBody @Valid Patient request, HttpServletRequest httpServletRequest) {
        try {
            String token = service.extractToken(httpServletRequest);
            User user = service.extractUserFromToken(token);
            validateLoggedInDoctorOrAdmin(user);

            return new ResponseEntity<>(patientService.addPatient(request), HttpStatus.CREATED);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + ex.getMessage());
        }
    }




    @GetMapping("/{UserID}")
    public ResponseEntity<Patient> getPatient(@PathVariable long UserID, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAllUser(user);

        Patient patient = patientService.findByPatientId(UserID);
        return ResponseEntity.ok(patient);
    }

}
