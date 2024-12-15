package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.ProcedureMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcedureMasterRepository extends JpaRepository<ProcedureMaster, String> {

    // Custom search query
    @Query("SELECT p FROM ProcedureMaster p WHERE " +
            "(:search IS NULL OR :search = '' OR " +
            "p.procedure_name LIKE %:search% OR " +
            "p.procedure_description LIKE %:search%)")
    Page<ProcedureMaster> searchProcedures(@Param("search") String search, Pageable pageable);
}
