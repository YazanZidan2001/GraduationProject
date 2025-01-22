package com.example.GraduationProject.Core.Repositories;


import com.example.GraduationProject.Common.Entities.DoseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DoseScheduleRepository extends JpaRepository<DoseSchedule, Long> {
    // Additional custom queries if needed
    void deleteByDoseDateBefore(LocalDate date);

}
