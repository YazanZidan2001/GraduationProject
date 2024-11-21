package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, String> {

    @Override
    Optional<Specialization> findById(String s);

    // Custom query for finding specialization by special_name
    @Query("SELECT s FROM Specialization s WHERE s.special_name = :special_name")
    Optional<Specialization> findBySpecialName(@Param("special_name") String special_name);

    // Custom query for checking existence by special_name
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Specialization s WHERE s.special_name = :special_name")
    boolean existsBySpecialName(@Param("special_name") String special_name);

}
