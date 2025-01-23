package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.MedicationsMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicationsMasterRepository extends JpaRepository<MedicationsMaster, Long> {

    // Simple search by scientificName or internationalName
    // using case-insensitive substring matching
    @Query("SELECT m FROM MedicationsMaster m " +
            "WHERE (:search IS NULL OR :search = '' " +
            "   OR LOWER(m.scientificName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "   OR LOWER(m.internationalName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<MedicationsMaster> searchMedications(@Param("search") String search);
}
