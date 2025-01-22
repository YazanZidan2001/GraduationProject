package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.ScheduleWorkTime;
import com.example.GraduationProject.Common.CompositeKey.ScheduleWorkTimeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleWorkTimeRepository extends JpaRepository<ScheduleWorkTime, ScheduleWorkTimeId> {

    @Query("SELECT s FROM ScheduleWorkTime s WHERE s.doctorId = :doctorId AND " +
            "(:date IS NULL OR (s.fromDate <= :date AND (s.toDate IS NULL OR s.toDate >= :date)))")
    Optional<ScheduleWorkTime> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    @Query("SELECT s FROM ScheduleWorkTime s WHERE s.doctorId = :doctorId AND s.clinicId = :clinicId")
    List<ScheduleWorkTime> findByDoctorIdAndClinicId(@Param("doctorId") Long doctorId, @Param("clinicId") Long clinicId);

    @Query("SELECT s FROM ScheduleWorkTime s WHERE s.doctorId = :doctorId " +
            "AND (s.fromDate <= :date AND (s.toDate IS NULL OR s.toDate >= :date))")
    Optional<ScheduleWorkTime> findScheduleByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);

    Optional<ScheduleWorkTime> findTopByOrderByScheduleIdDesc();

    @Query("SELECT s FROM ScheduleWorkTime s WHERE s.doctorId = :doctorId AND s.clinicId = :clinicId")
    List<ScheduleWorkTime> findScheduleByDoctorAndClinic(@Param("doctorId") Long doctorId, @Param("clinicId") Long clinicId);

}
