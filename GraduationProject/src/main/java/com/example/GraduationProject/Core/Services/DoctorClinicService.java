package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.CompositeKey.DoctorClinicId;
import com.example.GraduationProject.Core.Repositories.ClinicRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.DoctorClinic;
import com.example.GraduationProject.Core.Repositories.DoctorClinicRepository;
import com.example.GraduationProject.WebApi.Exceptions.DoctorClinicNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorClinicService {

    @Autowired
    private final DoctorClinicRepository doctorClinicRepository;

    @Transactional
    public void addDoctorClinic(DoctorClinic doctorClinic) {
        doctorClinicRepository.save(doctorClinic);
    }

    public List<Long> getClinicIdsByDoctorId(Long doctorId) {
        return doctorClinicRepository.findClinicIdsByDoctorId(doctorId);
    }

    public List<DoctorClinic> getAllDoctorClinicDetailsByDoctorId(Long doctorId) {
        return doctorClinicRepository.findAllWithClinicByDoctorId(doctorId);
    }

    @Transactional
    public void updateDoctorClinic(DoctorClinic doctorClinic, Long doctorId, Long clinicId) throws DoctorClinicNotFoundException {
        DoctorClinicId id = new DoctorClinicId(doctorId, clinicId);
        DoctorClinic existingDoctorClinic = doctorClinicRepository.findById(id)
                .orElseThrow(() -> new DoctorClinicNotFoundException("DoctorClinic not found with Doctor ID: " + doctorId + " and Clinic ID: " + clinicId));

        existingDoctorClinic.setStartDate(doctorClinic.getStartDate());
        existingDoctorClinic.setEndDate(doctorClinic.getEndDate());

        doctorClinicRepository.save(existingDoctorClinic);
    }

    @Transactional
    public DoctorClinic findDoctorClinic(Long doctorId, Long clinicId) throws DoctorClinicNotFoundException {
        DoctorClinicId id = new DoctorClinicId(doctorId, clinicId);
        return doctorClinicRepository.findById(id)
                .orElseThrow(() -> new DoctorClinicNotFoundException("DoctorClinic not found with Doctor ID: " + doctorId + " and Clinic ID: " + clinicId));
    }




    @Transactional
    public PaginationDTO<DoctorClinic> getAllDoctorClinics(int page, int size) {
        if (page < 1) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DoctorClinic> doctorClinics = doctorClinicRepository.findAll(pageable);

        PaginationDTO<DoctorClinic> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(doctorClinics.getTotalElements());
        paginationDTO.setTotalPages(doctorClinics.getTotalPages());
        paginationDTO.setSize(doctorClinics.getSize());
        paginationDTO.setNumber(doctorClinics.getNumber() + 1);
        paginationDTO.setNumberOfElements(doctorClinics.getNumberOfElements());
        paginationDTO.setContent(doctorClinics.getContent());

        return paginationDTO;
    }
}
