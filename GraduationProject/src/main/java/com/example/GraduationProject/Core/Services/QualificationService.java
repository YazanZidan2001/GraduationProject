package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.Qualification;
import com.example.GraduationProject.Core.Repositories.QualificationRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QualificationService {

    private final QualificationRepository qualificationRepository;

    // Add a new qualification
    public Qualification addQualification(Qualification qualification) {
        Long nextId = qualificationRepository.findMaxQualificationIdByDoctorId(qualification.getDoctorId());
        qualification.setQualificationId(nextId != null ? nextId + 1 : 1);  // Auto-increment logic
        return qualificationRepository.save(qualification);
    }

    // Get qualifications by doctor ID
    public List<Qualification> getQualificationsByDoctorId(Long doctorId) throws NotFoundException {
        List<Qualification> qualifications = qualificationRepository.findByDoctorId(doctorId);
//        if (qualifications.isEmpty()) {
//            throw new NotFoundException("No qualifications found for doctor ID " + doctorId);
//        }
        return qualifications;
    }

    // Get a qualification by its ID
    public Qualification getQualificationById(Long qualificationId) throws NotFoundException {
        return qualificationRepository.findById(qualificationId)
                .orElseThrow(() -> new NotFoundException("Qualification not found for ID " + qualificationId));
    }

    // Update an existing qualification
    public Qualification updateQualification(Long qualificationId, Qualification qualification) throws NotFoundException {
        if (!qualificationRepository.existsById(qualificationId)) {
            throw new NotFoundException("Qualification not found for ID " + qualificationId);
        }
        return qualificationRepository.save(qualification);
    }

    // Delete a qualification by ID
    public void deleteQualification(Long qualificationId) throws NotFoundException {
        if (!qualificationRepository.existsById(qualificationId)) {
            throw new NotFoundException("Qualification not found for ID " + qualificationId);
        }
        qualificationRepository.deleteById(qualificationId);
    }

    public List<Qualification> getAllQualifications() {
        return qualificationRepository.findAll();
    }
}
