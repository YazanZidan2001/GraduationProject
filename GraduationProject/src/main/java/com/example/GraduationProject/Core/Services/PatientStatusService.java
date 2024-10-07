package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.PatientStatus;
import com.example.GraduationProject.Core.Repositories.PatientStatusRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientStatusService {

    private final PatientStatusRepository patientStatusRepository;

    public PatientStatus addPatientStatus(PatientStatus patientStatus) {
        patientStatus.setInsertTime(LocalDateTime.now());
        return patientStatusRepository.save(patientStatus);
    }

    public PatientStatus updatePatientStatus(PatientStatus patientStatus) throws NotFoundException {
        if (patientStatus.getPatientId() == null || patientStatus.getDescription() == null) {
            throw new IllegalArgumentException("Patient ID and description cannot be null");
        }

        PatientStatus existingRecord = patientStatusRepository
                .findByPatient_PatientIdAndDescription(patientStatus.getPatientId(), patientStatus.getDescription())
                .orElseThrow(() -> new NotFoundException("Patient status not found"));

        existingRecord.setInsertTime(LocalDateTime.now());  // Update insert time
        existingRecord.setUserId(patientStatus.getUserId());

        return patientStatusRepository.save(existingRecord);
    }

    public List<PatientStatus> getStatusesByPatientId(Long patientId) throws NotFoundException {
        List<PatientStatus> statuses = patientStatusRepository.findByPatient_PatientId(patientId);
        if (statuses.isEmpty()) {
            throw new NotFoundException("No statuses found for patient ID " + patientId);
        }
        return statuses;
    }

    public void deletePatientStatus(Long patientId, String description) throws NotFoundException {
        PatientStatus patientStatus = patientStatusRepository
                .findByPatient_PatientIdAndDescription(patientId, description)
                .orElseThrow(() -> new NotFoundException("Patient status not found"));

        patientStatusRepository.delete(patientStatus);
    }
}
