package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Specialization;
import com.example.GraduationProject.Core.Repositories.SpecializationRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository specializationRepository;

    @Transactional
    public void addSpecialization(Specialization specialization) {
        specializationRepository.save(specialization);
    }

    @Transactional
    public PaginationDTO<Specialization> getAllSpecializationsBySearch(int page, int size, String search) {
        // Ensure page is 1-based
        if (page < 1) {
            page = 1;
        }

        // Normalize the search string
        if (search != null && search.isEmpty()) {
            search = null;
        }

        // Set up pagination
        Pageable pageable = PageRequest.of(page - 1, size);

        // Query the repository with pagination and search
        Page<Specialization> specializations = specializationRepository.searchSpecializations(pageable, search);

        // Build and return the PaginationDTO
        return PaginationDTO.<Specialization>builder()
                .totalElements(specializations.getTotalElements())
                .totalPages(specializations.getTotalPages())
                .size(specializations.getSize())
                .number(specializations.getNumber() + 1)
                .numberOfElements(specializations.getNumberOfElements())
                .content(specializations.getContent())
                .build();
    }


    @Transactional
    public void updateSpecialization(Specialization specialization) throws NotFoundException {
        specializationRepository.findById(specialization.getSpecial_name())
                .orElseThrow(() -> new NotFoundException("Specialization not found: " + specialization.getSpecial_name()));
        specializationRepository.save(specialization);
    }

    @Transactional
    public Specialization findSpecialization(String specialName) throws NotFoundException {
        return specializationRepository.findById(specialName)
                .orElseThrow(() -> new NotFoundException("Specialization not found: " + specialName));
    }

    @Transactional
    public List<Specialization> getAllSpecializations() {
        return specializationRepository.findAll();
    }
}
