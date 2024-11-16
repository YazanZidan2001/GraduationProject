package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.Doctor;
import com.example.GraduationProject.Common.Entities.Specialization;
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
            "(:specialization IS NULL OR d.specialization.special_name = :specialization)")
    Page<Doctor> findAll(Pageable pageable, @Param("search") String search, @Param("specialization") String specialization);

    // Find all doctors (if user is not deleted)
    @Query("SELECT d FROM Doctor d WHERE d.user.isDeleted = false")
    Page<Doctor> findAll(Pageable pageable);


    // Find doctors by search query and specialization (if user is not deleted)
    @Query("SELECT d FROM Doctor d WHERE d.user.isDeleted = false AND " +
            "(:search IS NULL OR :search = '' OR d.user.firstName LIKE %:search% OR d.user.lastName LIKE %:search% OR " +
            "d.user.email LIKE %:search% OR d.user.phone LIKE %:search%)")
    Page<Doctor> findAll(Pageable pageable, @Param("search") String search);
}
