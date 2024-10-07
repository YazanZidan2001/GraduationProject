package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Experience;
import com.example.GraduationProject.Common.CompositeKey.ExperienceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, ExperienceId> {
    List<Experience> findByDoctorId(Long doctorId);
}
