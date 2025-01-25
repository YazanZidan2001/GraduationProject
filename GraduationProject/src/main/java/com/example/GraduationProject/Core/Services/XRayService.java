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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    /**
     * New method:
     * Add multiple XRays in a single batch, also uploading result files (images/PDF).
     * The index i of 'files' should match the XRay at index i, if a file is present.
     */
    public void addMultipleXRaysWithFiles(List<XRay> xRays, List<MultipartFile> files) throws NotFoundException, IOException {
        // We assume the size of 'files' can match or be smaller/larger than xRays
        for (int i = 0; i < xRays.size(); i++) {
            XRay xRay = xRays.get(i);

            // 1) fetch the Visit to fill in patient/clinic/doctor
            Visit visit = visitRepository.findByVisitID(xRay.getVisitId())
                    .orElseThrow(() -> new NotFoundException("Visit not found with ID: " + xRay.getVisitId()));
            xRay.setClinicId(visit.getClinicId());
            xRay.setDoctorId(visit.getDoctorId());
            xRay.setPatientId(visit.getPatientId());

            // 2) Save the XRay (to get xrayId if needed for the file name)
            XRay savedXRay = xRayRepository.save(xRay);

            // 3) If there's a corresponding file, upload it
            if (files != null && i < files.size()) {
                MultipartFile file = files.get(i);
                if (file != null && !file.isEmpty()) {
                    uploadResultFileForXRay(savedXRay, file);
                }
            }
        }
    }

    /**
     * Helper method: store file on disk, set 'resultFilePath', and re-save
     */
    private void uploadResultFileForXRay(XRay xray, MultipartFile file) throws IOException {
        // Validate extension & size if needed
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!List.of("jpg", "jpeg", "png", "pdf").contains(extension)) {
            throw new IllegalArgumentException("Invalid file type. Allowed: JPG, JPEG, PNG, or PDF");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 10MB limit.");
        }

        // Create folder if not exist
        String folderName = "xray-results";
        File folder = Paths.get(System.getProperty("user.dir"), folderName).toFile();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create directory: " + folder.getAbsolutePath());
        }

        // Unique file name
        String uniqueName = "xray_" + xray.getXrayId() + "_" + System.currentTimeMillis() + "." + extension;
        File destFile = new File(folder, uniqueName);
        file.transferTo(destFile);

        // Set path in the entity
        String relativePath = folderName + "/" + uniqueName;
        xray.setResultFilePath(relativePath);

        // Update DB
        xRayRepository.save(xray);
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


    public XRay getXRayById(Long xrayId) {
        return xRayRepository.findById(xrayId)
                .orElseThrow(() -> new IllegalArgumentException("XRay not found with ID " + xrayId));
    }

    public XRay uploadXRayResultFile(Long xrayId, MultipartFile file) throws IOException {
        XRay xray = getXRayById(xrayId);

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!List.of("jpg", "jpeg", "png", "pdf").contains(extension)) {
            throw new IllegalArgumentException("Invalid file type. Allowed: JPG, JPEG, PNG, or PDF");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds the 10MB limit.");
        }

        String folderName = "xray-results";
        Path folderPath = Paths.get(System.getProperty("user.dir"), folderName);
        File folder = folderPath.toFile();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Could not create folder: " + folder.getAbsolutePath());
        }

        String uniqueName = "xray_" + xrayId + "_" + System.currentTimeMillis() + "." + extension;
        File destFile = new File(folder, uniqueName);
        file.transferTo(destFile);

        String relativePath = folderName + "/" + uniqueName;
        xray.setResultFilePath(relativePath);

        return xRayRepository.save(xray);
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
