package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientStatusRepository extends JpaRepository<PatientStatus, Long> {

    // Custom query to find patient statuses by patientId
    @Query("SELECT ps FROM PatientStatus ps WHERE ps.patientId = :patientId")
    List<PatientStatus> findAllByPatientId(@Param("patientId") Long patientId);

    // Find patient status by userId
    @Query("SELECT ps FROM PatientStatus ps WHERE ps.userId = :userId")
    List<PatientStatus> findAllByUserId(@Param("userId") Long userId);
}
