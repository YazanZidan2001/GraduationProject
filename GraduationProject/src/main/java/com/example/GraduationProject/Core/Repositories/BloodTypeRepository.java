package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.BloodType;
import com.example.GraduationProject.Common.Enums.BloodTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BloodTypeRepository extends JpaRepository<BloodType, BloodTypes> {
    // Optional: Find a blood type by its enum value
    Optional<BloodType> findByBloodType(BloodTypes bloodType);
}
