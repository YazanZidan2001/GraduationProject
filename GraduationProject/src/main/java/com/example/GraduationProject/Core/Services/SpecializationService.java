package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.Specialization;
import com.example.GraduationProject.Core.Repositories.SpecializationRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
