package com.example.GraduationProject.WebApi.Controllers.Admin.BloodType;


import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.BloodTypeService;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.Entities.BloodType;
import com.example.GraduationProject.Common.Enums.BloodTypes;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/blood-types")
@RequiredArgsConstructor
public class BloodTypeController extends SessionManagement {
    private final BloodTypeService bloodTypeService;
    private final AuthenticationService service;

    @PostMapping("/")
    public ResponseEntity<GeneralResponse> addBloodType(@RequestBody @Valid BloodType request, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        // Assuming validation for admin is implemented here
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        bloodTypeService.addBloodType(request);
        return ResponseEntity.ok(GeneralResponse.builder().message("Blood type added successfully").build());
    }

    @PutMapping("/")
    public ResponseEntity<GeneralResponse> updateBloodType(@RequestBody @Valid BloodType request, HttpServletRequest httpServletRequest) throws NotFoundException, UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        bloodTypeService.updateBloodType(request);
        return ResponseEntity.ok(GeneralResponse.builder().message("Blood type updated successfully").build());
    }

    @GetMapping("/{bloodType}")
    public ResponseEntity<BloodType> getBloodType(@PathVariable BloodTypes bloodType, HttpServletRequest httpServletRequest) throws NotFoundException, UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        BloodType type = bloodTypeService.findBloodType(bloodType);
        return ResponseEntity.ok(type);
    }

    @GetMapping("")
    public List<BloodType> getAllBloodTypes(HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);
        return bloodTypeService.getAllBloodTypes();
    }
}

