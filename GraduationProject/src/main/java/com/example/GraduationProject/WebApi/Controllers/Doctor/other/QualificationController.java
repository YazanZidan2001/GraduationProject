package com.example.GraduationProject.WebApi.Controllers.Doctor.other;

import com.example.GraduationProject.Common.Entities.Qualification;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.QualificationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors/qualifications")
@RequiredArgsConstructor
public class QualificationController extends SessionManagement {

    private final QualificationService qualificationService;
    private final AuthenticationService authenticationService;

    // Admin or Doctor can add a new qualification
    @PostMapping("/")
    public ResponseEntity<Qualification> addQualification(@RequestBody Qualification qualification, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInDoctorOrAdmin(user);

        // If the user is a doctor, ensure they can only add their own qualifications
        if (user.getRole().name().equalsIgnoreCase("DOCTOR")) {
            qualification.setDoctorId(user.getUserID());
        }

        Qualification newQualification = qualificationService.addQualification(qualification);
        return ResponseEntity.ok(newQualification);
    }

    // Admin or Doctor can update a qualification
    @PutMapping("/{qualificationId}")
    public ResponseEntity<Qualification> updateQualification(@PathVariable Long qualificationId, @RequestBody Qualification qualification, HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInDoctorOrAdmin(user);

        // If the user is a doctor, ensure they can only update their own qualifications
        if (user.getRole().name().equalsIgnoreCase("DOCTOR") && !qualification.getDoctorId().equals(user.getUserID())) {
            throw new UserNotFoundException("Doctors can only update their own qualifications");
        }

        Qualification updatedQualification = qualificationService.updateQualification(qualificationId, qualification);
        return ResponseEntity.ok(updatedQualification);
    }

    // Admin or Doctor can delete a qualification
    @DeleteMapping("/{qualificationId}")
    public ResponseEntity<String> deleteQualification(@PathVariable Long qualificationId, HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInDoctorOrAdmin(user);

        // If the user is a doctor, ensure they can only delete their own qualifications
        Qualification qualification = qualificationService.getQualificationById(qualificationId);
        if (user.getRole().name().equalsIgnoreCase("DOCTOR") && !qualification.getDoctorId().equals(user.getUserID())) {
            throw new UserNotFoundException("Doctors can only delete their own qualifications");
        }

        qualificationService.deleteQualification(qualificationId);
        return ResponseEntity.ok("Qualification deleted successfully");
    }

    // Admin can get all qualifications for a specific doctor
    @GetMapping("/all/{doctorId}")
    public ResponseEntity<List<Qualification>> getAllQualificationsForDoctor(@PathVariable Long doctorId, HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInAdmin(user);

        List<Qualification> qualifications = qualificationService.getQualificationsByDoctorId(doctorId);
        return ResponseEntity.ok(qualifications);
    }


    // Doctor can get their own qualifications
    @GetMapping("/my")
    public ResponseEntity<List<Qualification>> getDoctorQualifications(HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInDoctor(user);

        List<Qualification> qualifications = qualificationService.getQualificationsByDoctorId(user.getUserID());
        return ResponseEntity.ok(qualifications);
    }

    // Doctor can get a single qualification by ID (only their own)
    @GetMapping("/{qualificationId}")
    public ResponseEntity<Qualification> getDoctorQualificationById(@PathVariable Long qualificationId, HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInDoctorOrAdmin(user);

        Qualification qualification = qualificationService.getQualificationById(qualificationId);

        // Ensure the doctor can only retrieve their own qualifications
        if (!qualification.getDoctorId().equals(user.getUserID())) {
            throw new UserNotFoundException("Doctors can only view their own qualifications");
        }

        return ResponseEntity.ok(qualification);
    }
}
