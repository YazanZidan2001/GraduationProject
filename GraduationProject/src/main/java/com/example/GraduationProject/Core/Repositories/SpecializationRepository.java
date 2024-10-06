package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, String> {
//    Optional<Specialization> findBySpecial_name(String specialName); // Ensure this matches the field name

    @Override
    Optional<Specialization> findById(String s);
}
