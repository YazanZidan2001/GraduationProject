package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.LabTest;
import com.example.GraduationProject.Common.Entities.Visit;
import com.example.GraduationProject.Core.Repositories.LabTestRepository;
import com.example.GraduationProject.Core.Repositories.VisitRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LabTestService {

    private final LabTestRepository labTestRepository;
    private final VisitRepository visitRepository;

    public void addLabTest(LabTest labTest) throws NotFoundException {
        Visit visit = visitRepository.findByVisitID(labTest.getVisitId())
                .orElseThrow(() -> new NotFoundException("Visit not found with ID: " + labTest.getVisitId()));

        // Set clinic_id, doctor_id, patient_id from the Visit entity
        labTest.setClinicId(visit.getClinicId());
        labTest.setDoctorId(visit.getDoctorId());
        labTest.setPatientId(visit.getPatientId());
        labTestRepository.save(labTest);
    }

    public PaginationDTO<LabTest> getLabTestsByPatientId(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LabTest> labTestPage = labTestRepository.findByPatientId(patientId, pageable);
        return mapToPaginationDTO(labTestPage);
    }

    public PaginationDTO<LabTest> getLabTestsForPatientByVisitId(Long patientId, Long visitId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<LabTest> labTestPage = labTestRepository.findByVisitIdAndPatientId(visitId, patientId, pageable);
        return mapToPaginationDTO(labTestPage);
    }

    @Transactional
    public LabTest getLabTestForPatientById(Long patientId, Long testId) {
        return labTestRepository.findByTestIdAndPatientId(testId, patientId)
                .orElseThrow(() -> new IllegalArgumentException("Lab test not found for the patient."));
    }

    private PaginationDTO<LabTest> mapToPaginationDTO(Page<LabTest> page) {
        return PaginationDTO.<LabTest>builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber() + 1)
                .numberOfElements(page.getNumberOfElements())
                .content(page.getContent())
                .build();
    }

    @Transactional
    public LabTest getLabTestForPatientByVisitAndId(Long patientId, Long visitId, Long testId) {
        return labTestRepository.findByTestIdAndVisitIdAndPatientId(testId, visitId, patientId)
                .orElseThrow(() -> new IllegalArgumentException("Lab test not found for the patient and visit."));
    }
}
