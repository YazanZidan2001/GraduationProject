package com.example.GraduationProject.WebApi.Controllers.Admin.Specialization;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Common.Entities.Specialization;
import com.example.GraduationProject.Core.Services.SpecializationService;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/specializations")
@RequiredArgsConstructor
public class SpecializationController extends SessionManagement {
    private final SpecializationService specializationService;
    private final AuthenticationService service;

    @PostMapping("/")
    public ResponseEntity<GeneralResponse> addSpecialization(@RequestBody @Valid Specialization request, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        specializationService.addSpecialization(request);
        return ResponseEntity.ok(GeneralResponse.builder().message("Specialization added successfully").build());
    }


    @GetMapping("/getAllSpecializations")
    public PaginationDTO<Specialization> getAllSpecializations(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(defaultValue = "", required = false) String search,
                                                               HttpServletRequest httpServletRequest) throws UserNotFoundException {
        // Validate logged-in admin user
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        // Call service method to get paginated results
        return specializationService.getAllSpecializationsBySearch(page, size, search);
    }


    @PutMapping("/")
    public ResponseEntity<GeneralResponse> updateSpecialization(@RequestBody @Valid Specialization request, HttpServletRequest httpServletRequest) throws NotFoundException, UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        specializationService.updateSpecialization(request);
        return ResponseEntity.ok(GeneralResponse.builder().message("Specialization updated successfully").build());
    }

    @GetMapping("/{specialName}")
    public ResponseEntity<Specialization> getSpecialization(@PathVariable String specialName, HttpServletRequest httpServletRequest) throws NotFoundException, UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        Specialization specialization = specializationService.findSpecialization(specialName);
        return ResponseEntity.ok(specialization);
    }

    @GetMapping("")
    public List<Specialization> getAllSpecializations(HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAllUser(user);
        return specializationService.getAllSpecializations();
    }
}
