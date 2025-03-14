package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Appointment;
import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import com.example.GraduationProject.Common.Entities.Visit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, AppointmentCompositeKey> {

    // Find appointments by appointment ID
    Page<Appointment> findByAppointmentID(Long appointmentID, Pageable pageable);

    // Find appointments by doctor ID
    Page<Appointment> findByDoctorID(Long doctorID, Pageable pageable);

    Page<Appointment> findByDoctorIDAndAppointmentDate(Long doctorID, LocalDate appointmentDate, Pageable pageable);

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

    Page<Appointment> findByPatient_PatientIdAndIsCancelled(Long patientID, Boolean isCancelled, Pageable pageable);

    Page<Appointment> findByPatient_PatientIdAndIsDoneFalseAndIsCancelledFalse(Long patientID, Pageable pageable);

    Page<Appointment> findByPatientIDAndIsDoneAndIsCancelled(Long patientID, boolean isDone, boolean isCancelled, Pageable pageable);

    // Find appointments by appointment date
    Page<Appointment> findByAppointmentDate(LocalDate appointmentDate, Pageable pageable);

    // Find appointments by clinic ID
    Page<Appointment> findByClinic_ClinicId(Long clinicID, Pageable pageable);

    @Query(value = "SELECT * FROM appointment WHERE appointment_id = :appointmentId AND patient_id = :patientId", nativeQuery = true)
    Optional<Appointment> findAppointmentByAppointmentIdAndPatientId(@Param("appointmentId") Long appointmentId, @Param("patientId") Long patientId);

    public Optional<Appointment> findById(AppointmentCompositeKey id);

    @Query("SELECT a FROM Appointment a " +
            "JOIN a.patient p " +
            "JOIN p.user u " +
            "WHERE a.doctorID = :doctorID " +
            "AND (:date IS NULL OR a.appointmentDate = :date) " +
            "AND (:search IS NULL OR :search = '' " +
            "OR CAST(p.patientId AS string) LIKE %:search% " +
            "OR u.firstName LIKE %:search% " +
            "OR u.lastName LIKE %:search% " +
            "OR u.email LIKE %:search%)")
    Page<Appointment> searchAppointmentsByDoctorIDAndPatientDetailsAndDate(
            @Param("doctorID") Long doctorID,
            @Param("date") LocalDate date,
            @Param("search") String search,
            Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentID = :appointmentID AND a.doctorID = :doctorID")
    Optional<Appointment> findAppointmentByIdAndDoctorID(@Param("appointmentID") Long appointmentID, @Param("doctorID") Long doctorID);


    /**
     * Check if an appointment exists for a specific doctor, date, and time.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Appointment a " +
            "WHERE a.doctorID = :doctorID AND a.clinicID = :clinicID AND a.appointmentDate = :date AND a.appointmentTime = :time")
    boolean existsByDoctorIDAndAppointmentDateAndTime(
            @Param("doctorID") Long doctorID,
            @Param("clinicID") Long clinicID,
            @Param("date") LocalDate date,
            @Param("time") String time);


    /**
     * Check if a non-canceled appointment exists for a specific doctor, date, and time.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Appointment a " +
            "WHERE a.doctorID = :doctorID AND a.clinicID = :clinicID AND a.appointmentDate = :date AND a.appointmentTime = :time " +
            "AND a.isCancelled = false")
    boolean existsByDoctorIDAndAppointmentDateAndTimeAndIsCancelledFalse(
            @Param("doctorID") Long doctorID,
            @Param("clinicID") Long clinicID,
            @Param("date") LocalDate date,
            @Param("time") String time);


    Optional<Appointment> findTopByOrderByAppointmentIDDesc();



}
