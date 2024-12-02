package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.FamilyDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FamilyDiseaseRepository extends JpaRepository<FamilyDisease, Long> {

    List<FamilyDisease> findByPatient_PatientId(Long patientId);

    List<FamilyDisease> findByDisease_Name(String diseaseName);

    Optional<FamilyDisease> findByPatient_PatientIdAndFamilyMember(Long patientId, String familyMember);

    List<FamilyDisease> findByFamilyMember(String familyMember);
}
