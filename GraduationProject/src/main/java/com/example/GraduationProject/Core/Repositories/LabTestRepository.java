package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.LabTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, Long> {

    Page<LabTest> findByPatientId(Long patientId, Pageable pageable);

    Optional<LabTest> findByTestIdAndPatientId(Long testId, Long patientId);

    Page<LabTest> findByVisitIdAndPatientId(Long visitId, Long patientId, Pageable pageable);

    Optional<LabTest> findByTestIdAndVisitIdAndPatientId(Long testId, Long visitId, Long patientId);
}
