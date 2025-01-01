package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.ProcedureVisits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcedureVisitsRepository extends JpaRepository<ProcedureVisits, Long> {

    List<ProcedureVisits> findByVisit_PatientId(Long patientId);


    @Query("SELECT p FROM ProcedureVisits p WHERE p.visitID = :visitID")
    List<ProcedureVisits> findByVisitId(@Param("visitID") Long visitID);
}
