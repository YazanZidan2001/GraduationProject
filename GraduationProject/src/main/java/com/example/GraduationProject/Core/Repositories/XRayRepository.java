package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.XRay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface XRayRepository extends JpaRepository<XRay, Long> {

    Page<XRay> findByPatientId(Long patientId, Pageable pageable);

    Optional<XRay> findByXrayIdAndPatientId(Long xrayId, Long patientId);

    Page<XRay> findByVisitIdAndPatientId(Long visitId, Long patientId, Pageable pageable);

    Optional<XRay> findByXrayIdAndVisitIdAndPatientId(Long xrayId, Long visitId, Long patientId);
}
