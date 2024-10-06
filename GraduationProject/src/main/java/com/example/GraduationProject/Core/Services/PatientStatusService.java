package com.example.GraduationProject.Core.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.Entities.PatientStatus;
import com.example.GraduationProject.Core.Repositories.PatientStatusRepository;
import com.example.GraduationProject.WebApi.Exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientStatusService {

    private final PatientStatusRepository patientStatusRepository;

    // Method to add a new patient status
    @Transactional
    public PatientStatus addPatientStatus(PatientStatus patientStatus) {
        patientStatus.setInsertTime(LocalDate.now());
        return patientStatusRepository.save(patientStatus);
    }

    // Method to update an existing patient status by ID
    @Transactional
    public PatientStatus updatePatientStatus(Long statusId, PatientStatus updatedStatus) throws UserNotFoundException {
        PatientStatus patientStatus = patientStatusRepository.findById(statusId)
                .orElseThrow(() -> new UserNotFoundException("Patient status not found"));

        patientStatus.setDescription(updatedStatus.getDescription());
        patientStatus.setInsertTime(LocalDate.now());
        return patientStatusRepository.save(patientStatus);
    }

    // Get all statuses for a specific patient
    @Transactional
    public List<PatientStatus> getAllStatusesByPatientId(Long patientId) {
        return patientStatusRepository.findAllByPatientId(patientId);
    }

    // Get all statuses created by a specific user
    @Transactional
    public List<PatientStatus> getAllStatusesByUserId(Long userId) {
        return patientStatusRepository.findAllByUserId(userId);
    }
}
