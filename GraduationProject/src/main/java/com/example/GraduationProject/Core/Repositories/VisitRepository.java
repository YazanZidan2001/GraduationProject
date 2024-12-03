package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Appointment;
import com.example.GraduationProject.Common.Entities.Visit;
import com.example.GraduationProject.Common.CompositeKey.VisitCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, VisitCompositeKey> {


    @Query("SELECT v FROM Visit v " +
            "JOIN v.patient p " +
            "JOIN p.user u " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "u.firstName LIKE %:search% OR " +
            "u.lastName LIKE %:search% OR " +
            "u.email LIKE %:search% OR " +
            "u.phone LIKE %:search% OR " +
            "CAST(p.patientId AS string) LIKE %:search%)")
    Page<Visit> findByPatientFields(Pageable pageable, @Param("search") String search);

    Page<Visit> findByDoctorId(Long doctorId, Pageable pageable);

    Page<Visit> findByPatientId(Long patientId, Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.visitDate = :date")
    Page<Visit> findByDate(@Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.patientId = :patientID AND v.visitDate = :date")
    Page<Visit> findByPatientIdAndVisitDate(@Param("patientID") Long patientID, @Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT v FROM Visit v WHERE v.doctorId = :doctorID AND v.visitDate = :date")
    Page<Visit> findByDoctorIdAndVisitDate(@Param("doctorID") Long doctorID, @Param("date") LocalDate date, Pageable pageable);

    Optional<Visit> findTopByOrderByVisitIDDesc();



}
