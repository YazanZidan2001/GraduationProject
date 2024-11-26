package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Find doctor by user's email (if not deleted)
    @Query("SELECT d FROM Doctor d WHERE d.user.email = :email AND d.user.isDeleted = false")
    Optional<Doctor> findByUserEmail(@Param("email") String email);

    // Find doctor by doctor ID (if user is not deleted)
    @Query("SELECT d FROM Doctor d WHERE d.doctorId = :id AND d.user.isDeleted = false")
    Optional<Doctor> findById(@Param("id") Long id);

    // Find doctors by search query and specialization (if user is not deleted)
    @Query("SELECT d FROM Doctor d WHERE d.user.isDeleted = false AND " +
            "(:search IS NULL OR :search = '' OR d.user.firstName LIKE %:search% OR d.user.lastName LIKE %:search% OR " +
            "d.user.email LIKE %:search% OR d.user.phone LIKE %:search%) AND " +
            "(:specialization IS NULL OR LOWER(d.specialization.special_name) = LOWER(:specialization))")
    Page<Doctor> findAll_bySpecialization(Pageable pageable, @Param("search") String search, @Param("specialization") String specialization);



    // Find all doctors (if user is not deleted)
    @Query("SELECT d FROM Doctor d WHERE d.user.isDeleted = false")
    Page<Doctor> findAll(Pageable pageable);


    // Find doctors by search query and specialization (if user is not deleted)
    @Query("SELECT d FROM Doctor d WHERE d.user.isDeleted = false AND " +
            "(:search IS NULL OR :search = '' OR d.user.firstName LIKE %:search% OR d.user.lastName LIKE %:search% OR " +
            "d.user.email LIKE %:search% OR d.user.phone LIKE %:search%)")
    Page<Doctor> findAll_bySpecialization(Pageable pageable, @Param("search") String search);


    @Query("SELECT d FROM Doctor d WHERE d.user.isDeleted = false AND " +
            "(:search IS NULL OR :search = '' OR d.user.firstName LIKE %:search% OR d.user.lastName LIKE %:search% OR " +
            "d.user.email LIKE %:search% OR d.user.phone LIKE %:search%) AND " +
            "(:category IS NULL OR LOWER(d.specialization.category.category_name) = LOWER(:category))")
    Page<Doctor> findAll_byCategory(Pageable pageable, @Param("search") String search, @Param("category") String category);

    @Query("SELECT d FROM Doctor d " +
            "JOIN d.specialization s " +
            "JOIN s.category c " +
            "WHERE d.user.isDeleted = false AND " +
            "EXISTS (SELECT pd FROM PatientDisease pd " +
            "        JOIN pd.disease ds " +
            "        JOIN ds.category cat " +
            "        WHERE pd.patient.patientId = :patientId " +
            "        AND cat.category_name = c.category_name)")
    Page<Doctor> findDoctorsByPatientIdAndDiseases(Pageable pageable, @Param("patientId") Long patientId);

}
