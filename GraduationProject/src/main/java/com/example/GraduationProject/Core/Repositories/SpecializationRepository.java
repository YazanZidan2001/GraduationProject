package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    // Find specializations by search query (special_name or category_name)
    @Query("SELECT s FROM Specialization s WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "s.special_name LIKE %:search% OR s.category_name LIKE %:search%)")
    Page<Specialization> searchSpecializations(Pageable pageable, @Param("search") String search);

}
