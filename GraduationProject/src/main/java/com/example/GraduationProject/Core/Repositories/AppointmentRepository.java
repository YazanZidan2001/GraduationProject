package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Appointment;
import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, AppointmentCompositeKey> {

    // Find appointments by appointment ID
    Page<Appointment> findByAppointmentID(Long appointmentID, Pageable pageable);

    // Find appointments by doctor ID
    Page<Appointment> findByDoctorID(Long doctorID, Pageable pageable);

    // Find appointments by patient ID
    @Query("SELECT a FROM Appointment a " +
            "JOIN a.patient p " +
            "JOIN p.user u " +
            "WHERE (:search IS NULL OR :search = '' OR " +
            "u.firstName LIKE %:search% OR " +
            "u.lastName LIKE %:search% OR " +
            "u.email LIKE %:search% OR " +
            "u.phone LIKE %:search% OR " +
            "CAST(p.patientId AS string) LIKE %:search%)")
    Page<Appointment> findByPatientFields(Pageable pageable, @Param("search") String search);

    Page<Appointment> findByPatient_PatientId(Long patientId, Pageable pageable);




    // Find appointments by appointment date
    Page<Appointment> findByAppointmentDate(LocalDate appointmentDate, Pageable pageable);

    // Find appointments by clinic ID
    Page<Appointment> findByClinic_ClinicId(Long clinicID, Pageable pageable);
}
