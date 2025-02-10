package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.ProcedureVisits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcedureVisitsRepository extends JpaRepository<ProcedureVisits, Long> {

    List<ProcedureVisits> findByVisit_PatientId(Long patientId);


    @Query("SELECT p FROM ProcedureVisits p WHERE p.visitID = :visitID")
    List<ProcedureVisits> findByVisitId(@Param("visitID") Long visitID);

    // Get all procedures for a specific patient
    List<ProcedureVisits> findByPatientId(Long patientId);

    // Get all procedures for a specific visit and patient
    List<ProcedureVisits> findByVisitIDAndPatientId(Long visitID, Long patientId);

    // Get a specific procedure by procedure ID and patient ID
    Optional<ProcedureVisits> findByProcedureVisitIdAndPatientId(Long procedureVisitId, Long patientId);

    // Get a specific procedure for a specific visit and patient
    Optional<ProcedureVisits> findByVisitIDAndProcedureVisitIdAndPatientId(Long visitID, Long procedureVisitId, Long patientId);
}




