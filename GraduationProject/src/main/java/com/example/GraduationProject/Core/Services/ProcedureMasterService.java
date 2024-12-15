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
    public PaginationDTO<ProcedureMaster> getAllProcedures(String search, int page, int size) {
        // Define pagination
        Pageable pageable = PageRequest.of(page - 1, size);

        // Search procedures with the search query
        Page<ProcedureMaster> proceduresPage = procedureMasterRepository.searchProcedures(search, pageable);

        // Map Page to PaginationDTO
        return mapToPaginationDTO(proceduresPage);
    }

    private PaginationDTO<ProcedureMaster> mapToPaginationDTO(Page<ProcedureMaster> page) {
        return PaginationDTO.<ProcedureMaster>builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber() + 1) // Convert zero-based index to one-based index
                .numberOfElements(page.getNumberOfElements())
                .content(page.getContent())
                .build();
    }

}
