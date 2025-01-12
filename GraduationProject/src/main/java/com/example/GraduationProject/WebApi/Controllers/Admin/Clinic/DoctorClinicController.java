package com.example.GraduationProject.WebApi.Controllers.Admin.Clinic;

import com.example.GraduationProject.Common.Entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.DoctorClinic;
import com.example.GraduationProject.Core.Services.DoctorClinicService;
import com.example.GraduationProject.WebApi.Exceptions.DoctorClinicNotFoundException;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.SessionManagement;
import com.example.GraduationProject.Common.Responses.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/doctor-clinics")
@RequiredArgsConstructor
public class DoctorClinicController extends SessionManagement {

    @Autowired
    private final DoctorClinicService doctorClinicService;
    @Autowired
    private final AuthenticationService service;

    @PostMapping("/")
    public ResponseEntity<GeneralResponse> addDoctorClinic(@RequestBody @Valid DoctorClinic request, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        doctorClinicService.addDoctorClinic(request);
        return ResponseEntity.ok(GeneralResponse.builder().message("DoctorClinic added successfully").build());
    }

    @GetMapping("/clinic-ids/{doctorId}")
    public ResponseEntity<List<Long>> getClinicIdsByDoctorId(@PathVariable Long doctorId, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        // Extract and validate logged-in admin
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        // Fetch and return clinic IDs for the doctor
        List<Long> clinicIds = doctorClinicService.getClinicIdsByDoctorId(doctorId);
        return ResponseEntity.ok(clinicIds);
    }

    @GetMapping("/details/by-doctor/{doctorId}")
    public ResponseEntity<List<DoctorClinic>> getAllDetailsByDoctorId(@PathVariable Long doctorId, HttpServletRequest httpServletRequest) throws UserNotFoundException {
        // Extract and validate the logged-in user as admin
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAllUser(user);

        // Fetch and return the doctor-clinic details
        List<DoctorClinic> details = doctorClinicService.getAllDoctorClinicDetailsByDoctorId(doctorId);
        return ResponseEntity.ok(details);
    }


    @PutMapping("/{doctorId}/{clinicId}")
    public ResponseEntity<GeneralResponse> updateDoctorClinic(@PathVariable Long doctorId, @PathVariable Long clinicId, @RequestBody @Valid DoctorClinic request, HttpServletRequest httpServletRequest) throws DoctorClinicNotFoundException, UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        doctorClinicService.updateDoctorClinic(request, doctorId, clinicId);
        return ResponseEntity.ok(GeneralResponse.builder().message("DoctorClinic updated successfully").build());
    }

    @GetMapping("/{doctorId}/{clinicId}")
    public ResponseEntity<DoctorClinic> getDoctorClinic(@PathVariable Long doctorId, @PathVariable Long clinicId, HttpServletRequest httpServletRequest) throws DoctorClinicNotFoundException, UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        DoctorClinic doctorClinic = doctorClinicService.findDoctorClinic(doctorId, clinicId);
        return ResponseEntity.ok(doctorClinic);
    }

    @GetMapping("")
    public PaginationDTO<DoctorClinic> getAllDoctorClinics(@RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           HttpServletRequest httpServletRequest) throws UserNotFoundException {
        String token = service.extractToken(httpServletRequest);
        User user = service.extractUserFromToken(token);
        validateLoggedInAdmin(user);

        return doctorClinicService.getAllDoctorClinics(page, size);
    }

    @PutMapping("/{doctorId}/update-interval")
    public ResponseEntity<String> updateDoctorInterval(
            @PathVariable Long doctorId,
            @RequestParam Integer interval,
            HttpServletRequest request) throws UserNotFoundException, DoctorClinicNotFoundException {

        String token = service.extractToken(request);
        User user = service.extractUserFromToken(token);
        validateLoggedInDoctor(user);

        doctorClinicService.updateDoctorInterval(doctorId, interval);
        return ResponseEntity.ok("Interval updated successfully");
    }

}
