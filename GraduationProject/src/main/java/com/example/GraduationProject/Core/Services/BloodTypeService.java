package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Enums.BloodTypes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.Entities.BloodType;
import com.example.GraduationProject.Core.Repositories.BloodTypeRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BloodTypeService {
    private final BloodTypeRepository bloodTypeRepository;

    @Transactional
    public void addBloodType(BloodType bloodType) {
        bloodTypeRepository.save(bloodType);
    }

    @Transactional
    public void updateBloodType(BloodType bloodType) throws NotFoundException {
        bloodTypeRepository.findById(bloodType.getBloodType())
                .orElseThrow(() -> new NotFoundException("Blood type not found: " + bloodType.getBloodType()));
        bloodTypeRepository.save(bloodType);
    }

    @Transactional
    public BloodType findBloodType(BloodTypes bloodType) throws NotFoundException {
        return bloodTypeRepository.findById(bloodType)
                .orElseThrow(() -> new NotFoundException("Blood type not found: " + bloodType));
    }

    @Transactional
    public List<BloodType> getAllBloodTypes() {
        return bloodTypeRepository.findAll();
    }
}
