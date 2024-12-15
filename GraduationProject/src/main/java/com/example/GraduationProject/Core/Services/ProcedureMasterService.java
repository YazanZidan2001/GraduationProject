package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.ProcedureMaster;
import com.example.GraduationProject.Core.Repositories.ProcedureMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcedureMasterService {

    private final ProcedureMasterRepository procedureMasterRepository;

    @Transactional
    public void addProcedure(ProcedureMaster procedure) {
        // Save the new procedure
        procedureMasterRepository.save(procedure);
    }

    @Transactional
    public List<ProcedureMaster> getAllProcedures(String search) {
        // Check if a search term is provided
        if (search != null && !search.isEmpty()) {
            // Search procedures based on the search query
            return procedureMasterRepository.searchProceduresWithoutPagination(search);
        } else {
            // Fetch all procedures
            return procedureMasterRepository.findAll();
        }
    }


}
