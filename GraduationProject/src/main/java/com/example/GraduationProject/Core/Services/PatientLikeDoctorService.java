package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.patientLikeDoctor;
import com.example.GraduationProject.Core.Repositories.PatientLikeDoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientLikeDoctorService {

    @Autowired
    private PatientLikeDoctorRepository patientLikeDoctorRepository;

    public void likeDoctor(Long patientId, Long doctorId, boolean isLike) {
        // Check if the like/dislike already exists
        patientLikeDoctor existingLike = patientLikeDoctorRepository.findByPatientIdAndDoctorId(patientId, doctorId);

        if (existingLike != null) {
            // Update the existing record
            existingLike.setLike(isLike);
            patientLikeDoctorRepository.save(existingLike);
        } else {
            // Create a new record
            patientLikeDoctor newLike = patientLikeDoctor.builder()
                    .patientId(patientId)
                    .doctorId(doctorId)
                    .isLike(isLike)
                    .build();
            patientLikeDoctorRepository.save(newLike);
        }
    }

    public List<patientLikeDoctor> getAllLikedDoctorsByPatientId(Long patientId) {
        return patientLikeDoctorRepository.findAllLikedDoctorsByPatientId(patientId);
    }
}
