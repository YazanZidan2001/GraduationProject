package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.PatientStatus;
import com.example.GraduationProject.Common.CompositeKey.PatientStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientStatusRepository extends JpaRepository<PatientStatus, PatientStatusId> {

    List<PatientStatus> findByPatient_PatientId(Long patientId);

    Optional<PatientStatus> findByPatient_PatientIdAndDescription(Long patientId, String description);
}
