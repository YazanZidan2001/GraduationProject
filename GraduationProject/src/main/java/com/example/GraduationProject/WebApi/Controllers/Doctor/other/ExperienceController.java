package com.example.GraduationProject.WebApi.Controllers.Doctor.other;

import com.example.GraduationProject.Common.CompositeKey.ExperienceId;
import com.example.GraduationProject.Common.Entities.Experience;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.ExperienceService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctors/experiences")
@RequiredArgsConstructor
public class ExperienceController extends SessionManagement {

    private final ExperienceService experienceService;
    private final AuthenticationService authenticationService;

    // Admin or Doctor can add a new experience
    @PostMapping("/")
    public ResponseEntity<Experience> addExperience(@RequestBody Experience experience, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInDoctorOrAdmin(user);

        if (user.getRole().name().equalsIgnoreCase("DOCTOR")) {
            experience.setDoctorId(user.getUserID());
        }

        Experience newExperience = experienceService.addExperience(experience);
        return ResponseEntity.ok(newExperience);
    }

    // Admin or Doctor can update an experience
    @PutMapping("/{experienceId}")
    public ResponseEntity<Experience> updateExperience(@PathVariable ExperienceId experienceId, @RequestBody Experience experience, HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInDoctorOrAdmin(user);

        if (user.getRole().name().equalsIgnoreCase("DOCTOR") && !experience.getDoctorId().equals(user.getUserID())) {
            throw new UserNotFoundException("Doctors can only update their own experiences");
        }

        Experience updatedExperience = experienceService.updateExperience(experienceId, experience);
        return ResponseEntity.ok(updatedExperience);
    }

    // Admin or Doctor can delete an experience
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<String> deleteExperience(@PathVariable ExperienceId experienceId, HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInDoctorOrAdmin(user);

        Experience experience = experienceService.getExperienceById(experienceId);
        if (user.getRole().name().equalsIgnoreCase("DOCTOR") && !experience.getDoctorId().equals(user.getUserID())) {
            throw new UserNotFoundException("Doctors can only delete their own experiences");
        }

        experienceService.deleteExperience(experienceId);
        return ResponseEntity.ok("Experience deleted successfully");
    }

    // Admin can get all experiences for a specific doctor
    @GetMapping("/all/{doctorId}")
    public ResponseEntity<List<Experience>> getAllExperiencesForDoctor(@PathVariable Long doctorId, HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInAdmin(user);

        List<Experience> experiences = experienceService.getExperiencesByDoctorId(doctorId);
        return ResponseEntity.ok(experiences);
    }

    // Doctor can get their own experiences
    @GetMapping("/my")
    public ResponseEntity<List<Experience>> getDoctorExperiences(HttpServletRequest httpServletRequest) throws UserNotFoundException, NotFoundException {
        String token = authenticationService.extractToken(httpServletRequest);
        User user = authenticationService.extractUserFromToken(token);

        validateLoggedInDoctor(user);

        List<Experience> experiences = experienceService.getExperiencesByDoctorId(user.getUserID());
        return ResponseEntity.ok(experiences);
    }
}
