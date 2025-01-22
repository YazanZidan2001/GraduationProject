package com.example.GraduationProject.WebApi.Controllers.Visit.Prescription;


import com.example.GraduationProject.Common.Entities.Prescription;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.PrescriptionService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController extends SessionManagement {

    private final PrescriptionService prescriptionService;
    private final AuthenticationService authenticationService;


    @PostMapping
    public ResponseEntity<String> createPrescription(
            @RequestBody List<Prescription> prescriptionRequests,
            HttpServletRequest request
    ) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        // Set the userId on each prescription in the list
        for (Prescription p : prescriptionRequests) {
            p.setUserId(user.getUserID());
            p.setActive(true);
        }

        // Pass the entire list to the service
         prescriptionService.createPrescription(prescriptionRequests);

        // Return them all (could return a custom response object if you prefer)
        return ResponseEntity.status(HttpStatus.CREATED).body("Multiple Prescription added successfully");
    }


    // Additional endpoints, e.g.:
    // @GetMapping("/patient/{patientId}")
    // public List<Prescription> getPrescriptionsForPatient(@PathVariable Long patientId) { ... }

}

