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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

    @Transactional
    public void addMultipleLabTests(List<LabTest> labTests) throws NotFoundException {
        for (LabTest labTest : labTests) {
            Visit visit = visitRepository.findByVisitID(labTest.getVisitId())
                    .orElseThrow(() -> new NotFoundException("Visit not found with ID: " + labTest.getVisitId()));

            labTest.setClinicId(visit.getClinicId());
            labTest.setDoctorId(visit.getDoctorId());
            labTest.setPatientId(visit.getPatientId());

            labTestRepository.save(labTest);
        }
    }

    @Transactional
    public void addMultipleLabTestsWithFiles(List<LabTest> labTests, List<MultipartFile> files)
            throws NotFoundException, IOException {

        for (int i = 0; i < labTests.size(); i++) {
            LabTest labTest = labTests.get(i);

            // 1) Find Visit to auto-fill IDs
            Visit visit = visitRepository.findByVisitID(labTest.getVisitId())
                    .orElseThrow(() ->
                            new NotFoundException("Visit not found with ID: " + labTest.getVisitId()));

            labTest.setClinicId(visit.getClinicId());
            labTest.setDoctorId(visit.getDoctorId());
            labTest.setPatientId(visit.getPatientId());

            // 2) Save the LabTest record first (optional, can also do after the file step)
            LabTest saved = labTestRepository.save(labTest);

            // 3) If we have files, match by index and store resultFilePath
            if (files != null && i < files.size()) {
                MultipartFile file = files.get(i);
                if (file != null && !file.isEmpty()) {
                    // Upload logic: let's say we do a method: uploadResultFileForLabTest(...)
                    uploadResultFileForLabTest(saved, file);
                }
            }
        }
    }

    // Example sub-method to handle the actual file saving
    private void uploadResultFileForLabTest(LabTest labTest, MultipartFile file) throws IOException {

        String folderName = "lab-results";
        Path folderPath = Paths.get(System.getProperty("user.dir"), folderName);
        File folder = folderPath.toFile();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Failed to create folder: " + folder.getAbsolutePath());
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!List.of("jpg", "jpeg", "png", "pdf").contains(extension)) {
            throw new IllegalArgumentException("Invalid file type. Allowed: JPG, JPEG, PNG, or PDF");
        }
        // e.g. 10MB limit
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds the 10MB limit.");
        }

        // Unique file name
        String uniqueName = "labtest_" + labTest.getTestId() + "_" + System.currentTimeMillis() + "." + extension;
        File destFile = new File(folder, uniqueName);
        file.transferTo(destFile);

        // Store the path
        String relativePath = folderName + "/" + uniqueName;
        labTest.setResultFilePath(relativePath);

        // Save again (update the LabTest with the file path)
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


    public LabTest getLabTestById(Long testId) {
        return labTestRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("LabTest not found with ID " + testId));
    }

    public LabTest uploadLabResultFile(Long testId, MultipartFile file) throws IOException {
        // 1) Find the LabTest record
        LabTest labTest = getLabTestById(testId);

        // 2) Validate the file
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

        // 3) Create or ensure the folder
        String folderName = "lab-results";
        Path folderPath = Paths.get(System.getProperty("user.dir"), folderName);
        File folder = folderPath.toFile();
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Could not create folder: " + folder.getAbsolutePath());
        }

        // 4) Generate a unique file name
        String uniqueName = "labtest_" + testId + "_" + System.currentTimeMillis() + "." + extension;
        File destFile = new File(folder, uniqueName);

        // 5) Save the file to disk
        file.transferTo(destFile);

        // 6) Store only the path (relative or absolute)
        String relativePath = folderName + "/" + uniqueName;
        labTest.setResultFilePath(relativePath);

        // 7) Save
        return labTestRepository.save(labTest);
    }
}
