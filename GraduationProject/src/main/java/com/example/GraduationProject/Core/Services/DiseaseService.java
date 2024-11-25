package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.Disease;
import com.example.GraduationProject.Core.Repositories.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;

    // Add a new disease
    public Disease addDisease(Disease disease) {
        return diseaseRepository.save(disease);
    }

    public Disease updateDisease(String diseaseName, Disease updatedDisease) {
        return diseaseRepository.findById(diseaseName)
                .map(existingDisease -> {
                    // Update disease type
                    existingDisease.setDiseaseType(updatedDisease.getDiseaseType());

                    // Update remarks
                    existingDisease.setRemarks(updatedDisease.getRemarks());

                    // Update category if provided
                    if (updatedDisease.getCategory() != null) {
                        existingDisease.setCategory(updatedDisease.getCategory());
                    }

                    return diseaseRepository.save(existingDisease);
                })
                .orElseThrow(() -> new RuntimeException("Disease not found"));
    }

    // Get a disease by disease name
    public Disease getDiseaseByName(String diseaseName) {
        return diseaseRepository.findById(diseaseName)
                .orElseThrow(() -> new RuntimeException("Disease not found"));
    }

    // Get all diseases
    public List<Disease> getAllDiseases() {
        return diseaseRepository.findAll();
    }

    // Delete a disease by disease name
    public void deleteDisease(String diseaseName) {
        diseaseRepository.deleteById(diseaseName);
    }
}
