package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("SELECT p FROM Patient p WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "p.user.firstName LIKE %:search% OR p.user.lastName LIKE %:search% OR p.user.phone LIKE %:search%)")
    Page<Patient> findAll(Pageable pageable, @Param("search") String search);

    @Query("SELECT p FROM Patient p WHERE p.user.email = :email AND p.user.isDeleted = false")
    Optional<Patient> findByUserEmail(@Param("email") String email);

    @Query("SELECT p FROM Patient p WHERE p.patientId = :patientId")
    Optional<Patient> findByPatientId(@Param("patientId") Long patientId);
}
