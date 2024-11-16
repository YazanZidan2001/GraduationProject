package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    // Find a clinic by its name
    Optional<Clinic> findByClinicName(String clinicName);

    @Override
    Optional<Clinic> findById(Long aLong);
}
