package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.patientLikeDoctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientLikeDoctorRepository extends JpaRepository<patientLikeDoctor, Long> {

    // Fetch all doctors liked by a specific patient
    @Query("SELECT pld FROM patientLikeDoctor pld WHERE pld.patientId = :patientId AND pld.isLike = true")
    List<patientLikeDoctor> findAllLikedDoctorsByPatientId(Long patientId);

    // Find a specific like by patient and doctor IDs
    @Query("SELECT pld FROM patientLikeDoctor pld WHERE pld.patientId = :patientId AND pld.doctorId = :doctorId")
    patientLikeDoctor findByPatientIdAndDoctorId(Long patientId, Long doctorId);
}
