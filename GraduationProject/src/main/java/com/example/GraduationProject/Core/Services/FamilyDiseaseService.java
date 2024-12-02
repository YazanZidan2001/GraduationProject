package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.FamilyDisease;
import com.example.GraduationProject.Common.Entities.Disease;
import com.example.GraduationProject.Core.Repositories.FamilyDiseaseRepository;
import com.example.GraduationProject.Core.Repositories.DiseaseRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FamilyDiseaseService {

    private final FamilyDiseaseRepository familyDiseaseRepository;
    private final DiseaseRepository diseaseRepository;

    // Add a family disease record
    public FamilyDisease addFamilyDisease(FamilyDisease familyDisease) throws NotFoundException {
        Disease disease = diseaseRepository.findById(familyDisease.getDiseaseName())
                .orElseThrow(() -> new NotFoundException("Disease not found"));

        familyDisease.setDisease(disease);
        return familyDiseaseRepository.save(familyDisease);
    }

    // Update an existing family disease record
    public FamilyDisease updateFamilyDisease(FamilyDisease familyDisease) throws NotFoundException {
        FamilyDisease existingRecord = familyDiseaseRepository
                .findByPatient_PatientIdAndFamilyMember(familyDisease.getPatient().getPatientId(), familyDisease.getFamilyMember())
                .orElseThrow(() -> new NotFoundException("Family disease record not found"));

        existingRecord.setDiagnosisDate(familyDisease.getDiagnosisDate());
        existingRecord.setRemarks(familyDisease.getRemarks());
        existingRecord.setDiseaseType(familyDisease.getDiseaseType());
        existingRecord.setDisease(familyDisease.getDisease());

        return familyDiseaseRepository.save(existingRecord);
    }

    // Get all family diseases for a specific patient
    public List<FamilyDisease> getFamilyDiseasesByPatientId(Long patientId) throws NotFoundException {
        List<FamilyDisease> diseases = familyDiseaseRepository.findByPatient_PatientId(patientId);
        if (diseases.isEmpty()) {
            throw new NotFoundException("No family diseases found for patient with ID " + patientId);
        }
        return diseases;
    }

    // Get all patients with a specific disease
    public List<FamilyDisease> getPatientsWithDisease(String diseaseName) throws NotFoundException {
        List<FamilyDisease> patients = familyDiseaseRepository.findByDisease_Name(diseaseName);
        if (patients.isEmpty()) {
            throw new NotFoundException("No family diseases found with disease name " + diseaseName);
        }
        return patients;
    }

    // Delete a family disease record
    public void deleteFamilyDisease(Long familyDiseaseId) throws NotFoundException {
        FamilyDisease familyDisease = familyDiseaseRepository.findById(familyDiseaseId)
                .orElseThrow(() -> new NotFoundException("Family disease record not found with ID " + familyDiseaseId));

        familyDiseaseRepository.delete(familyDisease);
    }

    public List<FamilyDisease> getFamilyDiseasesByFamilyMember(String familyMember) throws NotFoundException {
        List<FamilyDisease> familyDiseases = familyDiseaseRepository.findByFamilyMember(familyMember);
        if (familyDiseases.isEmpty()) {
            throw new NotFoundException("No family diseases found for family member: " + familyMember);
        }
        return familyDiseases;
    }


    public Optional<FamilyDisease> getFamilyDiseasesByPatientAndFamilyMember(Long patientId, String familyMember) throws NotFoundException {
        Optional<FamilyDisease> familyDiseases = familyDiseaseRepository.findByPatient_PatientIdAndFamilyMember(patientId, familyMember);
        if (familyDiseases.isEmpty()) {
            throw new NotFoundException("No family diseases found for patient ID " + patientId + " and family member: " + familyMember);
        }
        return familyDiseases;
    }



}
