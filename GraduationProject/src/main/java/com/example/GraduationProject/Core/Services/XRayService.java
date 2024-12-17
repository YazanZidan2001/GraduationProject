package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Visit;
import com.example.GraduationProject.Common.Entities.XRay;
import com.example.GraduationProject.Core.Repositories.VisitRepository;
import com.example.GraduationProject.Core.Repositories.XRayRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class XRayService {

    private final XRayRepository xRayRepository;
    private final VisitRepository visitRepository;

    public void addXRay(XRay xRay) throws NotFoundException {

        Visit visit = visitRepository.findByVisitID(xRay.getVisitId())
                .orElseThrow(() -> new NotFoundException("Visit not found with ID: " + xRay.getVisitId()));

        // Set clinic_id, doctor_id, patient_id from the Visit entity
        xRay.setClinicId(visit.getClinicId());
        xRay.setDoctorId(visit.getDoctorId());
        xRay.setPatientId(visit.getPatientId());
        xRayRepository.save(xRay);
    }

    @Transactional
    public void addMultipleXRays(List<XRay> xRays) throws NotFoundException {
        for (XRay xRay : xRays) {
            Visit visit = visitRepository.findByVisitID(xRay.getVisitId())
                    .orElseThrow(() -> new NotFoundException("Visit not found with ID: " + xRay.getVisitId()));

            // Set clinic_id, doctor_id, patient_id from the Visit entity
            xRay.setClinicId(visit.getClinicId());
            xRay.setDoctorId(visit.getDoctorId());
            xRay.setPatientId(visit.getPatientId());

            xRayRepository.save(xRay);
        }
    }


    public PaginationDTO<XRay> getXRaysByPatientId(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<XRay> xRayPage = xRayRepository.findByPatientId(patientId, pageable);
        return mapToPaginationDTO(xRayPage);
    }

    public PaginationDTO<XRay> getXRaysForPatientByVisitId(Long patientId, Long visitId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<XRay> xRayPage = xRayRepository.findByVisitIdAndPatientId(visitId, patientId, pageable);
        return mapToPaginationDTO(xRayPage);
    }

    @Transactional
    public XRay getXRayForPatientById(Long patientId, Long xrayId) {
        return xRayRepository.findByXrayIdAndPatientId(xrayId, patientId)
                .orElseThrow(() -> new IllegalArgumentException("XRay not found for the patient."));
    }

    @Transactional
    public XRay getXRayForPatientByVisitAndId(Long patientId, Long visitId, Long xrayId) {
        return xRayRepository.findByXrayIdAndVisitIdAndPatientId(xrayId, visitId, patientId)
                .orElseThrow(() -> new IllegalArgumentException("XRay not found for the patient and visit."));
    }

    private PaginationDTO<XRay> mapToPaginationDTO(Page<XRay> page) {
        return PaginationDTO.<XRay>builder()
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber() + 1)
                .numberOfElements(page.getNumberOfElements())
                .content(page.getContent())
                .build();
    }
}
