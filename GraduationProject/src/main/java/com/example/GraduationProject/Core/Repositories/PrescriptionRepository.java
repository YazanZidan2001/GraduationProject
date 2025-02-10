package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {


    // Optionally, if you want a direct query for active prescriptions
// that haven't ended:
    @Query("SELECT p FROM Prescription p WHERE p.isActive = true AND p.endDate >= :today")
    List<Prescription> findActivePrescriptions(@Param("today") LocalDate today);

    // All prescriptions for a specific patient
    List<Prescription> findByPatientId(Long patientId);

    @Query("SELECT p FROM Prescription p WHERE p.patientId = :patientId AND p.endDate < :today")
            List<Prescription> findOldPrescriptions(@Param("patientId") Long patientId,
            @Param("today") LocalDate today);


    List<Prescription> findByVisitIdAndPatientId(Long visitId, Long patientId);

    Optional<Prescription> findByVisitIdAndPrescriptionIdAndPatientId(Long visitId, Long prescriptionId, Long patientId);

    Optional<Prescription> findByPrescriptionIdAndPatientId(Long prescriptionId, Long patientId);
}
