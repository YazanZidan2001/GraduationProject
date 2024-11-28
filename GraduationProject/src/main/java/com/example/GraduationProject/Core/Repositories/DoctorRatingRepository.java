package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.DoctorRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRatingRepository extends JpaRepository<DoctorRating, Long> {
    List<DoctorRating> findByDoctor_DoctorId(Long doctorId);

    @Query("SELECT AVG(r.ratingValue) FROM DoctorRating r WHERE r.doctorId = :doctorId")
    Double calculateAverageRating(@Param("doctorId") Long doctorId);
}

