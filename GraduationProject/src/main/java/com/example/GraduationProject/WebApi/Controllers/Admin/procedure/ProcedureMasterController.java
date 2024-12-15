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

    @GetMapping("/")
    public ResponseEntity<List<ProcedureMaster>> getAllProcedures(
            @RequestParam(required = false) String search) {
        // Fetch all procedures with optional search
        List<ProcedureMaster> procedures = procedureMasterService.getAllProcedures(search);

        // Return the response
        return ResponseEntity.ok(procedures);
    }


}
