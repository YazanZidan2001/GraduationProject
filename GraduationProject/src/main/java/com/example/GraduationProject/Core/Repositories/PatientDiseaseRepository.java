package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.PatientDisease;
import com.example.GraduationProject.Common.CompositeKey.PatientDiseaseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientDiseaseRepository extends JpaRepository<PatientDisease, PatientDiseaseId> {

    List<PatientDisease> findByPatient_PatientId(Long patientId);

    List<PatientDisease> findByDisease_Name(String diseaseName);

    Optional<PatientDisease> findByPatient_PatientIdAndDisease_Name(Long patientId, String diseaseName);
}

