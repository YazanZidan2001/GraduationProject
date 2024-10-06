package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, String> {
    // You can add custom query methods here if needed

    @Override
    Optional<Disease> findById(String s);
}
