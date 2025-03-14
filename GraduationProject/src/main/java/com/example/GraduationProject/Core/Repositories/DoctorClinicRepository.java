package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.CompositeKey.DoctorClinicId;
import com.example.GraduationProject.Common.Entities.Doctor;
import com.example.GraduationProject.Common.Entities.DoctorClinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorClinicRepository extends JpaRepository<DoctorClinic, DoctorClinicId> {
    @Query("SELECT dc FROM DoctorClinic dc JOIN FETCH dc.clinic c WHERE dc.doctorId = :doctorId")
    List<DoctorClinic> findAllWithClinicByDoctorId(Long doctorId);

    @Query("SELECT dc.clinicId FROM DoctorClinic dc WHERE dc.doctorId = :doctorId")
    List<Long> findClinicIdsByDoctorId(Long doctorId);

    boolean existsByDoctorIdAndClinicId(Long doctorId, Long clinicId);

    @Query("SELECT dc.clinicId FROM DoctorClinic dc WHERE dc.doctorId = :doctorId")
    Optional<Long> findClinicIdByDoctorId(@Param("doctorId") Long doctorId);

    Optional<DoctorClinic> findByDoctorIdAndClinicId(Long doctorId, Long clinicId);

    @Query("SELECT dc FROM DoctorClinic dc WHERE dc.doctorId = :doctorId AND dc.isActive = true")
    Optional<DoctorClinic> findByDoctorIdAndIsActiveTrue(@Param("doctorId") Long doctorId);

    @Query("SELECT dc.doctor FROM DoctorClinic dc WHERE dc.clinicId = :clinicId")
    List<Doctor> findDoctorsByClinicId(@Param("clinicId") Long clinicId);

    @Query("SELECT COUNT(dc) > 0 FROM DoctorClinic dc WHERE dc.doctorId = :doctorId AND dc.clinicId = :clinicId")
    boolean existsByDoctorAndClinic(@Param("doctorId") Long doctorId, @Param("clinicId") Long clinicId);



}
