package com.example.GraduationProject.WebApi.Controllers.Admin.procedure;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.ProcedureMaster;
import com.example.GraduationProject.Core.Services.ProcedureMasterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/procedures")
@RequiredArgsConstructor
public class ProcedureMasterController {

    private final ProcedureMasterService procedureMasterService;

    // Add a new procedure
    @PostMapping("/")
    public ResponseEntity<String> addProcedure(@RequestBody ProcedureMaster procedure) {
        procedureMasterService.addProcedure(procedure);
        return ResponseEntity.status(HttpStatus.CREATED).body("Procedure added successfully");
    }

    /**
     * Add multiple procedures
     * Expects a JSON array of ProcedureMaster objects in the request body.
     */
    @PostMapping("/bulk")
    public ResponseEntity<?> addMultipleProcedures(@RequestBody List<ProcedureMaster> procedures) {
        try {
            List<ProcedureMaster> savedList = procedureMasterService.addMultipleProcedures(procedures);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedList);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to add multiple procedures: " + ex.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllProcedures(
            @RequestParam(required = false) String search) {
        try {
            List<ProcedureMaster> procedures = procedureMasterService.getAllProcedures(search);
            if (procedures.isEmpty()) {
                return ResponseEntity.ok("No procedures found.");
            }
            return ResponseEntity.ok(procedures);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error fetching procedures: " + ex.getMessage());
        }
    }


}
