package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.Disease;
import com.example.GraduationProject.Common.Entities.PatientDisease;
import com.example.GraduationProject.Common.CompositeKey.PatientDiseaseId;
import com.example.GraduationProject.Core.Repositories.DiseaseRepository;
import com.example.GraduationProject.Core.Repositories.PatientDiseaseRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientDiseaseService {

    private final PatientDiseaseRepository patientDiseaseRepository;
    private final DiseaseRepository diseaseRepository;

    public PatientDisease addPatientDisease(PatientDisease patientDisease) throws NotFoundException {
        Disease disease = diseaseRepository.findById(patientDisease.getDiseaseName())
                .orElseThrow(() -> new NotFoundException("Disease not found"));

        patientDisease.setDisease(disease);
        patientDisease.setInsertTime(LocalDateTime.now());

        return patientDiseaseRepository.save(patientDisease);
    }



    // Update an existing patient disease record
    public PatientDisease updatePatientDisease(PatientDisease patientDisease) throws NotFoundException {
        // Ensure the patient disease exists before updating
        if (patientDisease.getPatientId() == null || patientDisease.getDiseaseName() == null) {
            throw new IllegalArgumentException("Patient ID and disease name cannot be null");
        }

        // Check if the record exists
        PatientDisease existingRecord = patientDiseaseRepository
                .findByPatient_PatientIdAndDisease_Name(patientDisease.getPatientId(), patientDisease.getDiseaseName())
                .orElseThrow(() -> new NotFoundException("Patient disease not found for patient ID " + patientDisease.getPatientId() + " and disease " + patientDisease.getDiseaseName()));

        // Update fields as needed (excluding the ID)
        existingRecord.setDiseaseDate(patientDisease.getDiseaseDate());
        existingRecord.setInsertTime(LocalDateTime.now()); // Update insert time if needed
        existingRecord.setRemarks(patientDisease.getRemarks());
        existingRecord.setUserId(patientDisease.getUserId());

        // Save the updated record
        return patientDiseaseRepository.save(existingRecord);
    }

    // Get all diseases for a specific patient
    public List<PatientDisease> getDiseasesByPatientId(Long patientId) throws NotFoundException {
        List<PatientDisease> diseases = patientDiseaseRepository.findByPatient_PatientId(patientId);
//        if (diseases.isEmpty()) {
//            throw new NotFoundException("No diseases found for patient with ID " + patientId);
//        }
        return diseases;
    }

    // Get all patients with a specific disease
    public List<PatientDisease> getPatientsWithDisease(String diseaseName) throws NotFoundException {
        List<PatientDisease> patients = patientDiseaseRepository.findByDisease_Name(diseaseName);
        if (patients.isEmpty()) {
            throw new NotFoundException("No patients found with disease " + diseaseName);
        }
        return patients;
    }

    // Delete a patient disease record
    public void deletePatientDisease(Long patientId, String diseaseName) throws NotFoundException {
        // Fetch the patient disease record by patient ID and disease name
        PatientDisease patientDisease = patientDiseaseRepository.findByPatient_PatientIdAndDisease_Name(patientId, diseaseName)
                .orElseThrow(() -> new NotFoundException("Patient disease not found for patient ID " + patientId + " and disease " + diseaseName));

        // Perform deletion
        try {
            patientDiseaseRepository.delete(patientDisease);
        } catch (Exception e) {
            // Handle any deletion issues
            throw new RuntimeException("Error while deleting patient disease: " + e.getMessage());
        }
    }
}
