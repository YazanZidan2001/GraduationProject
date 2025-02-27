package com.example.GraduationProject.Core.Services;


import com.example.GraduationProject.Common.Entities.DoseSchedule;
import com.example.GraduationProject.Common.Entities.LabTest;
import com.example.GraduationProject.Common.Entities.Prescription;
import com.example.GraduationProject.Common.Entities.Visit;
import com.example.GraduationProject.Core.Repositories.DoseScheduleRepository;
import com.example.GraduationProject.Core.Repositories.PrescriptionRepository;
import com.example.GraduationProject.Core.Repositories.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final DoseScheduleRepository doseScheduleRepository;
    private final VisitRepository visitRepository;

    public List<Prescription> createPrescription(List<Prescription> prescriptionRequests) {
        // Prepare a list to hold all the saved prescriptions
        List<Prescription> savedPrescriptions = new java.util.ArrayList<>();

        // Loop through each prescription in the request
        for (Prescription prescriptionRequest : prescriptionRequests) {

            // 1. Fetch the Visit by visitId
            Long visitId = prescriptionRequest.getVisitId();
            Visit visit = visitRepository.findByVisitID(visitId)
                    .orElseThrow(() -> new IllegalArgumentException("Visit not found for visitId = " + visitId));

            // 2. Auto-fill the prescription fields from the Visit
            prescriptionRequest.setClinicId(visit.getClinicId());
            prescriptionRequest.setDoctorId(visit.getDoctorId());
            prescriptionRequest.setPatientId(visit.getPatientId());

            // (Optional) Link if you want the relationship
//            prescriptionRequest.setVisit(visit);

            // 3. Compute endDate if not provided
            if (prescriptionRequest.getEndDate() == null) {
                Integer totalDays = prescriptionRequest.getTotalDays();
                Integer dayInterval = prescriptionRequest.getDayInterval();

                if (totalDays == null || totalDays <= 0) {
                    throw new IllegalArgumentException(
                            "totalDays must be provided and > 0 if endDate is not specified."
                    );
                }
                // endDate = startDate + (dayInterval * totalDays) - 1
                LocalDate computedEnd = prescriptionRequest.getStartDate()
                        .plusDays((long) dayInterval * totalDays - 1);

                prescriptionRequest.setEndDate(computedEnd);
            }

            // 4. Ensure insertTime is set (to today) if not set
            if (prescriptionRequest.getInsertTime() == null) {
                prescriptionRequest.setInsertTime(LocalDate.now());
            }

            // 5. Save the Prescription
            Prescription savedPrescription = prescriptionRepository.save(prescriptionRequest);

            // 6) Generate today's DoseSchedule ignoring dayInterval
            createSameDayDoseSchedules(savedPrescription);

            // 7. Add to our list
            savedPrescriptions.add(savedPrescription);
        }

        // After processing all prescriptions, return the entire list
        return savedPrescriptions;
    }

    /**
     * Creates dose schedules for the current day (i.e., the day the prescription is created),
     * **without** checking dayInterval. Only checks if:
     *   - prescription is active
     *   - today is >= startDate && <= endDate
     */
    private void createSameDayDoseSchedules(Prescription prescription) {
        LocalDate today = LocalDate.now();

        // Check if prescription is active and "today" is within the date range
        if (!prescription.isActive()) return;
        if (today.isBefore(prescription.getStartDate()) || today.isAfter(prescription.getEndDate())) {
            return;
        }

        // Create rows for morning/noon/evening if selected
        if (prescription.isMorningDose()) {
            createDoseSchedule(prescription, today, LocalTime.of(7, 0));
        }
        if (prescription.isNoonDose()) {
            createDoseSchedule(prescription, today, LocalTime.of(14, 0));
        }
        if (prescription.isEveningDose()) {
            createDoseSchedule(prescription, today, LocalTime.of(20, 0));
        }
    }

    private void createDoseSchedule(Prescription p, LocalDate date, LocalTime time) {
        DoseSchedule ds = new DoseSchedule();
        ds.setPrescription(p);
        ds.setMedicationId(p.getMedicationId());
        ds.setDoseDate(date);
        ds.setDoseTime(time);
        ds.setPatientId(p.getPatientId());
        ds.setNotificationSent(false);
        ds.setDoseTaken(false);

        doseScheduleRepository.save(ds);
    }

    // Return all prescriptions for a given patient
    public List<Prescription> findAllByPatient(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }


    /**
     * Returns all prescriptions for the given patient
     */
    public List<Prescription> getAllMedicationsForPatient(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    /**
     * Returns active (and not old) prescriptions for the given patient.
     * "not old" means we also check if endDate >= today's date, if that’s your definition.
     */
    public List<Prescription> getActiveMedicationsForPatient(Long patientId) {
        List<Prescription> allForPatient = prescriptionRepository.findByPatientId(patientId);
        LocalDate today = LocalDate.now();

        return allForPatient.stream()
                // isActive == true
                .filter(p -> p.isActive())
                // Also check if endDate >= now, so it’s not “old”
                .filter(p -> !p.getEndDate().isBefore(today))
                .toList();
    }

    /**
     * Returns inactive or old prescriptions for the given patient.
     * This typically means isActive = false or the endDate has passed.
     */
    public List<Prescription> getInactiveMedicationsForPatient(Long patientId) {
        List<Prescription> allForPatient = prescriptionRepository.findByPatientId(patientId);
        LocalDate today = LocalDate.now();

        return allForPatient.stream()
                // isActive == false OR endDate < now
                .filter(p -> !p.isActive() )
                .toList();
    }

    /**
     * Returns OLD prescriptions for the given patient.
     * "Old" here means endDate is strictly before today's date,
     * regardless of isActive or not.
     */
    public List<Prescription> getOldMedicationsForPatient(Long patientId) {
        LocalDate today = LocalDate.now();
        List<Prescription> allForPatient = prescriptionRepository.findByPatientId(patientId);

        return allForPatient.stream()
                // old if endDate < today
                .filter(p -> p.getEndDate() != null && p.getEndDate().isBefore(today))
                .toList();
    }

    public List<Prescription> findByVisitIdAndPatientId(Long visitId, Long patientId) {
        return prescriptionRepository.findByVisitIdAndPatientId(visitId, patientId);
    }

    public Prescription findByVisitIdAndPrescriptionId(Long visitId, Long prescriptionId, Long patientId) {
        return prescriptionRepository.findByVisitIdAndPrescriptionIdAndPatientId(visitId, prescriptionId, patientId)
                .orElse(null);
    }

    public Prescription findByPrescriptionIdAndPatientId(Long prescriptionId, Long patientId) {
        return prescriptionRepository.findByPrescriptionIdAndPatientId(prescriptionId, patientId)
                .orElse(null);
    }





    @Transactional
    public void deactivateMedication(Long prescriptionId, Long doctorUserId) {
        // 1) Fetch the prescription
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No prescription found with ID " + prescriptionId));

        // 2) Check if the prescription belongs to the same doctor
        //    Typically, you compare the doctor’s user ID or the doctor ID field on the Prescription
        if (!prescription.getDoctorId().equals(doctorUserId)) {
            throw new IllegalArgumentException(
                    "You do not have permission to deactivate this prescription."
            );
        }

        // 3) Set isActive to false
        prescription.setActive(false);

        // 4) Save
        prescriptionRepository.save(prescription);
    }




}
