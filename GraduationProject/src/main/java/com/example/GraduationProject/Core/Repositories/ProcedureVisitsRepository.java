package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.ProcedureVisits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcedureVisitsRepository extends JpaRepository<ProcedureVisits, Long> {

    List<ProcedureVisits> findByVisit_PatientId(Long patientId);

}
