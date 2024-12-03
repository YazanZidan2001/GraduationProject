package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.CompositeKey.DoctorClinicId;
import com.example.GraduationProject.Common.Entities.DoctorClinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorClinicRepository extends JpaRepository<DoctorClinic, DoctorClinicId> {
    @Query("SELECT dc FROM DoctorClinic dc JOIN FETCH dc.clinic c WHERE dc.doctorId = :doctorId")
    List<DoctorClinic> findAllWithClinicByDoctorId(Long doctorId);

    @Query("SELECT dc.clinicId FROM DoctorClinic dc WHERE dc.doctorId = :doctorId")
    List<Long> findClinicIdsByDoctorId(Long doctorId);
}
