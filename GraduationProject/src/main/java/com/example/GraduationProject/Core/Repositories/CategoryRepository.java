package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    // Custom query for checking existence by special_name
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Category s WHERE s.category_name = :category_name")
    boolean existsByCategoryName(@Param("category_name") String category_name);

    // Custom search by category_name
    @Query("SELECT c FROM Category c " +
            "WHERE (:search IS NULL OR :search = '' " +
            "   OR LOWER(c.category_name) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Category> searchCategories(@Param("search") String search);
}
