package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.CompositeKey.DoctorClinicId;
import com.example.GraduationProject.Common.Entities.DoctorClinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorClinicRepository extends JpaRepository<DoctorClinic, DoctorClinicId> {
    // Additional query methods can be defined here if needed
}
