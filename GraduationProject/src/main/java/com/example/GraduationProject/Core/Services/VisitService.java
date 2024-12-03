package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.CompositeKey.VisitCompositeKey;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Visit;
import com.example.GraduationProject.Core.Repositories.VisitRepository;
import com.example.GraduationProject.WebApi.Exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class VisitService {


    @Autowired
    private final VisitRepository visitRepository;
    @Autowired
    private final DoctorClinicService doctorClinicService;

    @Transactional
    public void addVisit(Visit visit, Long doctorIdFromToken) {
        // Fetch the last visit ID from the repository
        Long lastVisitId = visitRepository.findTopByOrderByVisitIDDesc()
                .map(Visit::getVisitID)
                .orElse(0L); // Default to 0 if no visits exist

        // Set the new visit ID
        visit.setVisitID(lastVisitId + 1);

        // Automatically set the visit date to the current date
        visit.setVisitDate(LocalDate.now());

        // Set the doctor ID from the token
        visit.setDoctorId(doctorIdFromToken);


        visit.setClinicId(doctorClinicService.getClinicIdsByDoctorId(doctorIdFromToken).getFirst());

        // Save the new visit
        visitRepository.save(visit);
    }


    @Transactional
    public PaginationDTO<Visit> searchVisitsByPatientFields(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Visit> visits = visitRepository.findByPatientFields(pageable, search);
        return mapToPaginationDTO(visits);
    }

    @Transactional
    public PaginationDTO<Visit> findVisitsByDoctor(Long doctorId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Visit> visits = visitRepository.findByDoctorId(doctorId, pageable);
        return mapToPaginationDTO(visits);
    }

    @Transactional
    public PaginationDTO<Visit> findVisitsByPatient(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Visit> visits = visitRepository.findByPatientId(patientId, pageable);
        return mapToPaginationDTO(visits);
    }

    @Transactional
    public PaginationDTO<Visit> findVisitsByPatientID(Long patientID, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Visit> visits = visitRepository.findByPatientId(patientID, pageable);

        return mapToPaginationDTO(visits);
    }

    @Transactional
    public PaginationDTO<Visit> findVisitsByDate(LocalDate date, int page, int size) throws NotFoundException {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Visit> visits = visitRepository.findByDate(date, pageable);

        if (visits.isEmpty()) {
            throw new NotFoundException("No visits found for the given date: " + date);
        }

        return mapToPaginationDTO(visits);
    }

    @Transactional
    public Visit findVisitById(VisitCompositeKey visitCompositeKey) throws NotFoundException {
        return visitRepository.findById(visitCompositeKey)
                .orElseThrow(() -> new NotFoundException("Visit not found with the given ID."));
    }


    @Transactional
    public PaginationDTO<Visit> findVisitsByPatientAndDate(Long patientID, LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Visit> visits = visitRepository.findByPatientIdAndVisitDate(patientID, date, pageable);
        return mapToPaginationDTO(visits);
    }

    @Transactional
    public PaginationDTO<Visit> findVisitsByDoctorAndDate(Long doctorID, LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Visit> visits = visitRepository.findByDoctorIdAndVisitDate(doctorID, date, pageable);
        return mapToPaginationDTO(visits);
    }


    @Transactional
    public PaginationDTO<Visit> getAllVisits(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Visit> visits = visitRepository.findAll(pageable);

        return mapToPaginationDTO(visits);
    }

    private PaginationDTO<Visit> mapToPaginationDTO(Page<Visit> page) {
        PaginationDTO<Visit> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(page.getTotalElements());
        paginationDTO.setTotalPages(page.getTotalPages());
        paginationDTO.setSize(page.getSize());
        paginationDTO.setNumber(page.getNumber() + 1);
        paginationDTO.setNumberOfElements(page.getNumberOfElements());
        paginationDTO.setContent(page.getContent());
        return paginationDTO;
    }
}
