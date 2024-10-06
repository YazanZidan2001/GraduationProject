package com.example.GraduationProject.WebApi.Controllers.Admin.Disease;

import com.example.GraduationProject.Common.Entities.Disease;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import com.example.GraduationProject.Core.Services.DiseaseService;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/diseases")
@RequiredArgsConstructor
public class DiseaseController extends SessionManagement {

    private final DiseaseService diseaseService;
    private final AuthenticationService authenticationService;

    @PostMapping("/")
    public ResponseEntity<GeneralResponse> addDisease(@RequestBody @Valid Disease disease, HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        Disease savedDisease = diseaseService.addDisease(disease);
        return ResponseEntity.ok(GeneralResponse.builder().message("Disease added successfully").build());
    }

    @PutMapping("/{diseaseName}")
    public ResponseEntity<GeneralResponse> updateDisease(@PathVariable String diseaseName, @RequestBody @Valid Disease updatedDisease, HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        diseaseService.updateDisease(diseaseName, updatedDisease);
        return ResponseEntity.ok(GeneralResponse.builder().message("Disease updated successfully").build());
    }

    @GetMapping("/{diseaseName}")
    public ResponseEntity<Disease> getDiseaseByName(@PathVariable String diseaseName, HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        Disease disease = diseaseService.getDiseaseByName(diseaseName);
        return ResponseEntity.ok(disease);
    }

    @GetMapping("/")
    public ResponseEntity<List<Disease>> getAllDiseases(HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        List<Disease> diseases = diseaseService.getAllDiseases();
        return ResponseEntity.ok(diseases);
    }

    @DeleteMapping("/{diseaseName}")
    public ResponseEntity<GeneralResponse> deleteDisease(@PathVariable String diseaseName, HttpServletRequest request) throws UserNotFoundException {
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        diseaseService.deleteDisease(diseaseName);
        return ResponseEntity.ok(GeneralResponse.builder().message("Disease deleted successfully").build());
    }
}
