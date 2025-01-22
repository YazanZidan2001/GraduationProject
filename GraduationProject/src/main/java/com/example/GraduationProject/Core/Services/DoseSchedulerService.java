package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.DoseSchedule;
import com.example.GraduationProject.Common.Entities.Prescription;
import com.example.GraduationProject.Core.Repositories.DoseScheduleRepository;
import com.example.GraduationProject.Core.Repositories.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoseSchedulerService {

    private final PrescriptionRepository prescriptionRepository;
    private final DoseScheduleRepository doseScheduleRepository;

    /**
     * Runs daily at midnight (00:00).
     * CRON: second minute hour day-of-month month day-of-week
     * "0 0 0 * * ?" = 00:00 every day
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyDoseScheduleJob() {
        LocalDate today = LocalDate.now();

        // 1. Generate today's DoseSchedule rows for due prescriptions
        generateTodayDoses(today);

        // 2. Delete old DoseSchedule rows (e.g., anything before 'today')
        deleteOldDoses(today);
    }

    /**
     * Find all active prescriptions for which "today" is a scheduled dose day.
     * Then create the dose rows (morning/noon/evening).
     */
    private void generateTodayDoses(LocalDate today) {
        // Get all prescriptions (or query only active ones).
        // Then filter: isActive, within [startDate, endDate], and dayInterval match.
        List<Prescription> allPrescriptions = prescriptionRepository.findAll();

        // Filter them down
        List<Prescription> activeDuePrescriptions = allPrescriptions.stream()
                .filter(p -> p.isActive()
                        && !today.isBefore(p.getStartDate())
                        && !today.isAfter(p.getEndDate()))
                .filter(p -> {
                    // days since start:
                    long daysSinceStart = ChronoUnit.DAYS.between(p.getStartDate(), today);
                    // Check if (daysSinceStart % dayInterval) == 0
                    return (daysSinceStart % p.getDayInterval() == 0);
                })
                .toList();

        // For each prescription, create dose schedules for morning/noon/evening if needed
        for (Prescription p : activeDuePrescriptions) {
            if (p.isMorningDose()) {
                createDoseSchedule(p, today, LocalTime.of(7, 0));
            }
            if (p.isNoonDose()) {
                createDoseSchedule(p, today, LocalTime.of(14, 0));
            }
            if (p.isEveningDose()) {
                createDoseSchedule(p, today, LocalTime.of(20, 0));
            }
        }
    }

    /**
     * Deletes DoseSchedule rows whose date is before 'today'.
     * That means we only keep today's (and possibly future if you ever create them).
     *
     * If you want EXACT 24-hour windows, you'd need a more detailed approach
     * comparing date+time. This example is simpler: anything older than "today" is removed.
     */
    private void deleteOldDoses(LocalDate today) {
        doseScheduleRepository.deleteByDoseDateBefore(today);
    }

    /**
     * Creates a single DoseSchedule entry in the DB
     */
    private void createDoseSchedule(Prescription p, LocalDate date, LocalTime time) {
        DoseSchedule ds = new DoseSchedule();
        ds.setPrescription(p);
        ds.setMedicationId(p.getMedicationId());
        ds.setDoseDate(date);
        ds.setDoseTime(time);
        ds.setNotificationSent(false);
        ds.setDoseTaken(false);

        doseScheduleRepository.save(ds);
    }
}
