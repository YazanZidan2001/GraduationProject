package com.example.GraduationProject.Core.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Clinic;
import com.example.GraduationProject.Core.Repositories.ClinicRepository;
import com.example.GraduationProject.WebApi.Exceptions.ClinicNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicService {
    private final ClinicRepository clinicRepository;

    @Transactional
    public void addClinic(Clinic clinic) {
        clinicRepository.save(clinic);
    }

    @Transactional
    public void updateClinic(Clinic clinic, Long clinicId) throws ClinicNotFoundException {
        Clinic existingClinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ClinicNotFoundException("Clinic not found with ID: " + clinicId));

        existingClinic.setClinicName(clinic.getClinicName());
        existingClinic.setAddress(clinic.getAddress());

        clinicRepository.save(existingClinic);
    }

    @Transactional
    public Clinic findClinicById(Long clinicId) throws ClinicNotFoundException {
        return clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ClinicNotFoundException("Clinic not found with ID: " + clinicId));
    }

    @Transactional
    public PaginationDTO<Clinic> getAllClinics(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Clinic> clinics = clinicRepository.findAll(pageable);

        PaginationDTO<Clinic> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(clinics.getTotalElements());
        paginationDTO.setTotalPages(clinics.getTotalPages());
        paginationDTO.setSize(clinics.getSize());
        paginationDTO.setNumber(clinics.getNumber() + 1);
        paginationDTO.setNumberOfElements(clinics.getNumberOfElements());
        paginationDTO.setContent(clinics.getContent());

        return paginationDTO;
    }
}
