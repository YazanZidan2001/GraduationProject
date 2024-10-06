package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Long> {

    // Custom query to find qualifications by doctor ID
    List<Qualification> findByDoctorId(Long doctorId);

}
