package com.example.GraduationProject.WebApi.Controllers.Visit.Prescription;

import com.example.GraduationProject.Common.Entities.DoseSchedule;
import com.example.GraduationProject.Common.Entities.Prescription;
import com.example.GraduationProject.Common.Entities.User;
import com.example.GraduationProject.Core.Services.AuthenticationService;
import com.example.GraduationProject.Core.Services.DoseSchedulerService;
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
@RequestMapping("/prescriptions/dose")
@RequiredArgsConstructor
public class DoseController extends SessionManagement {

    private final DoseSchedulerService doseScheduleService;
    private final AuthenticationService authenticationService;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayDoses(HttpServletRequest request) throws UserNotFoundException {
        // 1) Extract user from token
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);

        // 2) Validate that the user is a patient
        validateLoggedInPatient(user);

        // 3) Get the patient's ID (assuming user.getPatient().getPatientId() or user.getUserID() if they match)
        Long patientId = user.getUserID();

        try {
            // 4) Fetch today's doses
            List<DoseSchedule> doses = doseScheduleService.getTodayDosesForPatient(patientId);

            if (doses.isEmpty()) {
                return ResponseEntity.ok("No doses for today.");
            }
            return ResponseEntity.ok(doses);

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving today's doses: " + ex.getMessage());
        }
    }

    @GetMapping("/today/untaken")
    public ResponseEntity<?> getUnTakenDoses(HttpServletRequest request) throws UserNotFoundException {
        // 1) استخراج المستخدم من التوكن
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);

        // 2) التأكد أن المستخدم هو مريض
        validateLoggedInPatient(user);

        // 3) جلب ID المريض
        Long patientId = user.getUserID();

        try {
            // 4) جلب الجرعات غير المأخوذة
            List<DoseSchedule> doses = doseScheduleService.getUnTakenDosesForToday(patientId);

            if (doses.isEmpty()) {
                return ResponseEntity.ok("No untaken doses for today.");
            }
            return ResponseEntity.ok(doses);

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving untaken doses: " + ex.getMessage());
        }
    }


    @PutMapping("/{doseId}/mark-as-taken")
    public ResponseEntity<?> markDoseAsTaken(@PathVariable Long doseId, HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        // 1) استخراج المستخدم من التوكن
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);

        // 2) التأكد أن المستخدم هو مريض
        validateLoggedInPatient(user);

        // 3) البحث عن الجرعة
        DoseSchedule dose = doseScheduleService.findById(doseId)
                .orElseThrow(() -> new NotFoundException("Dose not found"));

        // 4) تحديث الجرعة إلى "تم أخذها"
        dose.setDoseTaken(true);
        doseScheduleService.save(dose);

        return ResponseEntity.ok("Dose marked as taken.");
    }

    @PutMapping("/{doseId}/mark-as-not-taken")
    public ResponseEntity<?> markDoseAsNotTaken(@PathVariable Long doseId, HttpServletRequest request) throws UserNotFoundException, NotFoundException {
        // 1) استخراج المستخدم من التوكن
        String token = authenticationService.extractToken(request);
        User user = authenticationService.extractUserFromToken(token);

        // 2) التأكد أن المستخدم هو مريض
        validateLoggedInPatient(user);

        // 3) البحث عن الجرعة
        DoseSchedule dose = doseScheduleService.findById(doseId)
                .orElseThrow(() -> new NotFoundException("Dose not found"));

        // 4) تحديث الجرعة إلى "تم أخذها"
        dose.setDoseTaken(false);
        doseScheduleService.save(dose);

        return ResponseEntity.ok("Dose marked as not taken.");
    }

}

