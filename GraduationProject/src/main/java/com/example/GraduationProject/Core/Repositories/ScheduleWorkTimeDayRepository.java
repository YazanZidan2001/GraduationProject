package com.example.GraduationProject.Core.Repositories;

import com.example.GraduationProject.Common.Entities.ScheduleWorkTimeDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleWorkTimeDayRepository extends JpaRepository<ScheduleWorkTimeDay, Long> {
}
