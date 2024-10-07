package com.example.GraduationProject.WebApi.Controllers.Patient.other;

import com.example.GraduationProject.Common.Entities.PatientStatus;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.PatientStatusService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/patients/statuses")
@RequiredArgsConstructor
public class PatientStatusController extends SessionManagement {

    private final PatientStatusService patientStatusService;
    private final AuthenticationService authenticationService;

    @PostMapping("/")
    public ResponseEntity<PatientStatus> addPatientStatus(@RequestBody @Valid PatientStatus request, HttpServletRequest httpServletRequest)
            throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT") && !user.getUserID().equals(request.getPatientId())) {
            throw new UserNotFoundException("Patients can only add their own information");
        }

        request.setUserId(user.getUserID());

        PatientStatus response = patientStatusService.addPatientStatus(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<PatientStatus> updatePatientStatus(@RequestBody @Valid PatientStatus request, HttpServletRequest httpServletRequest)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT") && !user.getUserID().equals(request.getPatientId())) {
            throw new UserNotFoundException("Patients can only update their own information");
        }

        request.setUserId(user.getUserID());

        PatientStatus response = patientStatusService.updatePatientStatus(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<PatientStatus>> getStatusesByPatientId(@PathVariable Long patientId, HttpServletRequest httpServletRequest)
            throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT") && !user.getUserID().equals(patientId)) {
            throw new UserNotFoundException("You can only view your own information");
        }

        List<PatientStatus> statuses = patientStatusService.getStatusesByPatientId(patientId);
        return ResponseEntity.ok(statuses);
    }

    @DeleteMapping("/{patientId}/{description}")
    public ResponseEntity<GeneralResponse> deletePatientStatus(@PathVariable Long patientId, @PathVariable String description, HttpServletRequest httpServletRequest)
            throws NotFoundException, UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInPatientAndDoctor(user);
        if (user.getRole().name().equalsIgnoreCase("PATIENT") && !user.getUserID().equals(patientId)) {
            throw new UserNotFoundException("Patients can only delete their own information");
        }

        patientStatusService.deletePatientStatus(patientId, description);
        return ResponseEntity.ok(new GeneralResponse("Patient status record deleted successfully"));
    }
}
