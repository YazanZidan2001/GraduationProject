package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.Entities.ProcedureVisits;
import com.example.GraduationProject.Common.Entities.Visit;
import com.example.GraduationProject.Core.Repositories.ProcedureVisitsRepository;
import com.example.GraduationProject.Core.Repositories.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcedureVisitsService {

    private final ProcedureVisitsRepository procedureVisitsRepository;
    private final VisitRepository visitRepository;

    @Transactional
    public void addProcedureVisit(ProcedureVisits procedureVisits) throws NotFoundException{
        // Fetch the Visit entity using visitID
        Visit visit = visitRepository.findByVisitID(procedureVisits.getVisitID())
                .orElseThrow(() -> new NotFoundException("Visit not found with ID: " + procedureVisits.getVisitID()));

        // Set clinic_id, doctor_id, patient_id from the Visit entity
        procedureVisits.setClinicId(visit.getClinicId());
        procedureVisits.setDoctorId(visit.getDoctorId());
        procedureVisits.setPatientId(visit.getPatientId());

        // Set the current time for insertTime
        procedureVisits.setInsertTime(LocalDateTime.now());

        // Save ProcedureVisits
        procedureVisitsRepository.save(procedureVisits);
    }

    @Transactional
    public void addMultipleProcedureVisits(List<ProcedureVisits> procedureVisitsList) throws NotFoundException {
        for (ProcedureVisits procedureVisits : procedureVisitsList) {
            Visit visit = visitRepository.findByVisitID(procedureVisits.getVisitID())
                    .orElseThrow(() -> new NotFoundException("Visit not found with ID: " + procedureVisits.getVisitID()));

            procedureVisits.setClinicId(visit.getClinicId());
            procedureVisits.setDoctorId(visit.getDoctorId());
            procedureVisits.setPatientId(visit.getPatientId());
            procedureVisits.setInsertTime(LocalDateTime.now());

            procedureVisitsRepository.save(procedureVisits);
        }
    }


    @Transactional
    public List<ProcedureVisits> getProceduresByPatientId(Long patientId) {
        return procedureVisitsRepository.findByVisit_PatientId(patientId);
    }

    @Transactional(readOnly = true)
    public List<ProcedureVisits> getAllProcedureVisits() {
        return procedureVisitsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ProcedureVisits> getProceduresByVisitId(Long visitID) throws NotFoundException {
        // Fetch the visit entity
        Visit visit = visitRepository.findByVisitID(visitID)
                .orElseThrow(() -> new NotFoundException("Visit not found with ID: " + visitID));


        // Fetch and return procedures by visit ID
        return procedureVisitsRepository.findByVisitId(visitID);
    }


    /**
     * Get all procedures for a specific patient.
     */
    public List<ProcedureVisits> getProceduresForPatient(Long patientId) {
        return procedureVisitsRepository.findByPatientId(patientId);
    }

    /**
     * Get all procedures for a specific visit and patient.
     */
    public List<ProcedureVisits> getProceduresForVisitForPatient(Long visitID, Long patientId) {
        return procedureVisitsRepository.findByVisitIDAndPatientId(visitID, patientId);
    }

    /**
     * Get a specific procedure by procedure ID and patient ID.
     */
    public ProcedureVisits getProcedureByIdForPatient(Long procedureVisitId, Long patientId) {
        return procedureVisitsRepository.findByProcedureVisitIdAndPatientId(procedureVisitId, patientId)
                .orElse(null);
    }

    /**
     * Get a specific procedure for a specific visit and patient.
     */
    public ProcedureVisits getProcedureForVisitForPatient(Long visitID, Long procedureVisitId, Long patientId) {
        return procedureVisitsRepository.findByVisitIDAndProcedureVisitIdAndPatientId(visitID, procedureVisitId, patientId)
                .orElse(null);
    }

}



