package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.XRay;
import com.example.GraduationProject.Core.Repositories.XRayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class XRayService {

    private final XRayRepository xRayRepository;

    public void addXRay(XRay xRay) {
        xRayRepository.save(xRay);
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
