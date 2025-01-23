package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.MedicationsMaster;
import com.example.GraduationProject.Core.Repositories.MedicationsMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicationsMasterService {

    private final MedicationsMasterRepository medicationsMasterRepository;

    /**
     * Add a single medication
     */
    public MedicationsMaster addMedication(MedicationsMaster medication) {
        // validation logic if needed
        return medicationsMasterRepository.save(medication);
    }

    /**
     * Add multiple medications
     */
    public List<MedicationsMaster> addMultipleMedications(List<MedicationsMaster> medications) {
        // validation logic if needed
        return medicationsMasterRepository.saveAll(medications);
    }

    /**
     * Get all medications (with optional search filter)
     */
    public List<MedicationsMaster> getAllMedications(String search) {
        // If you want to return all if no search,
        // we can simply pass search to the custom query:
        return medicationsMasterRepository.searchMedications(search);
    }
}
