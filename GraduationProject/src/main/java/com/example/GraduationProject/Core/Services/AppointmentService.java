package com.example.GraduationProject.Core.Services;

import com.example.GraduationProject.Common.CompositeKey.AppointmentCompositeKey;
import com.example.GraduationProject.Common.DTOs.PaginationDTO;
import com.example.GraduationProject.Common.Entities.Appointment;
import com.example.GraduationProject.Common.Entities.Clinic;
import com.example.GraduationProject.Common.Entities.DoctorClinic;
import com.example.GraduationProject.Core.Repositories.AppointmentRepository;
import com.example.GraduationProject.Core.Repositories.ClinicRepository;
import com.example.GraduationProject.Core.Repositories.DoctorClinicRepository;
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
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private  final ClinicRepository  clinicRepository;
    private final AppointmentNotificationService AppNotificationService;
    @Autowired
    private DoctorClinicRepository doctorClinicRepository;
    @Transactional
    public void addAppointment(Appointment appointment) throws NotFoundException {
        Long clinicID = appointment.getClinicID();
        Clinic clinic = clinicRepository.findById(clinicID)
                .orElseThrow(() -> new NotFoundException("Clinic not found with ID: " + clinicID));

        appointment.setClinic(clinic);
        appointmentRepository.save(appointment);

        // Create a notification for this appointment
        AppNotificationService.createNotification(appointment);
    }





    @Transactional
    public void updateAppointment(Appointment appointment, Long appointmentID, Long doctorID, Long patientID, Long clinicID) throws NotFoundException {
        AppointmentCompositeKey id = new AppointmentCompositeKey(appointmentID, doctorID, patientID, clinicID);
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found with Appointment ID: " + appointmentID
                        + ", Doctor ID: " + doctorID + ", Patient ID: " + patientID + ", and Clinic ID: " + clinicID));

        existingAppointment.setAppointmentDate(appointment.getAppointmentDate());
        existingAppointment.setAppointmentTime(appointment.getAppointmentTime());
        existingAppointment.setIsDone(appointment.getIsDone());

        appointmentRepository.save(existingAppointment);
    }


    @Transactional
    public Appointment findAppointment(Long appointmentID, Long doctorID, Long patientID, Long clinicID) throws NotFoundException {
        AppointmentCompositeKey id = new AppointmentCompositeKey(appointmentID, doctorID, patientID, clinicID);
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment not found with Appointment ID: " + appointmentID
                        + ", Doctor ID: " + doctorID + ", Patient ID: " + patientID + ", and Clinic ID: " + clinicID));
    }

    @Transactional
    public PaginationDTO<Appointment> findByDoctorID(Long doctorID, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = (Page<Appointment>) appointmentRepository.findByDoctorID(doctorID, pageable);

        return mapToPaginationDTO(appointments);
    }

    @Transactional
    public PaginationDTO<Appointment> findByPatientID(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = (Page<Appointment>) appointmentRepository.findByPatientFields(pageable, search);

        return mapToPaginationDTO(appointments);
    }

    @Transactional
    public PaginationDTO<Appointment> findAppointmentsForLoggedInPatient(Long patientID, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = appointmentRepository.findByPatient_PatientId(patientID, pageable);
        return mapToPaginationDTO(appointments);
    }


    @Transactional
    public PaginationDTO<Appointment> findByAppointmentDate(LocalDate appointmentDate, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = (Page<Appointment>) appointmentRepository.findByAppointmentDate(appointmentDate, pageable);

        return mapToPaginationDTO(appointments);
    }

    @Transactional
    public PaginationDTO<Appointment> findByClinicID(Long clinicID, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = (Page<Appointment>) appointmentRepository.findByClinic_ClinicId(clinicID, pageable);

        return mapToPaginationDTO(appointments);
    }

    @Transactional
    public PaginationDTO<Appointment> findByAppointmentID(Long appointmentID, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = appointmentRepository.findByAppointmentID(appointmentID, pageable);

        return mapToPaginationDTO(appointments);
    }

    @Transactional
    public PaginationDTO<Appointment> getAllAppointments(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = appointmentRepository.findAll(pageable);

        return mapToPaginationDTO(appointments);
    }

    @Transactional
    public void cancelAppointmentByPatient(Long appointmentID, Long patientID) throws NotFoundException {
        // Find the appointment using appointmentID and patientID
        Appointment appointment = appointmentRepository.findAppointmentByAppointmentIdAndPatientId(appointmentID, patientID)
                .orElseThrow(() -> new NotFoundException("Appointment not found with Appointment ID: " + appointmentID + " and Patient ID: " + patientID));

        // Update only the isCancelled field
        appointment.setIsCancelled(true);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void cancelAppointmentByDoctor(Long appointmentID, Long patientID) throws NotFoundException {
        // Find the appointment using appointmentID and patientID
        Appointment appointment = appointmentRepository.findAppointmentByAppointmentIdAndPatientId(appointmentID, patientID)
                .orElseThrow(() -> new NotFoundException("Appointment not found with Appointment ID: " + appointmentID + " and Patient ID: " + patientID));

        // Update only the isCancelled field
        appointment.setIsCancelled(true);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public DoctorClinic getDoctorClinicFromAppointment(Long appointmentID) throws NotFoundException {
        // Retrieve the appointment to find doctorId and clinicId
        Appointment appointment = appointmentRepository.findById(new AppointmentCompositeKey(appointmentID, null, null, null))
                .orElseThrow(() -> new NotFoundException("Appointment not found with Appointment ID: " + appointmentID));

        // Find the DoctorClinic entry based on doctorId and clinicId
        return doctorClinicRepository.findByDoctorIdAndClinicId(appointment.getDoctorID(), appointment.getClinicID())
                .orElseThrow(() -> new NotFoundException("DoctorClinic not found for Doctor ID: " + appointment.getDoctorID() + " and Clinic ID: " + appointment.getClinicID()));
    }


    @Transactional
    public PaginationDTO<Appointment> getAppointmentsByStatus(Long patientID, boolean isCancelled, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = appointmentRepository.findByPatient_PatientIdAndIsCancelled(patientID, isCancelled, pageable);
        return mapToPaginationDTO(appointments);
    }

    public PaginationDTO<Appointment> getAppointmentsByStatus(Long patientID, boolean isDone, boolean isCancelled, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = appointmentRepository.findByPatientIDAndIsDoneAndIsCancelled(patientID, isDone, isCancelled, pageable);

        return mapToPaginationDTO(appointments);
    }


    @Transactional
    public PaginationDTO<Appointment> getActiveAppointmentsByPatient(Long patientID, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Appointment> appointments = appointmentRepository.findByPatient_PatientIdAndIsDoneFalseAndIsCancelledFalse(patientID, pageable);
        return mapToPaginationDTO(appointments);
    }


    @Transactional
    public PaginationDTO<Appointment> getTodayAppointmentsForDoctor(Long doctorID, int page, int size) {
        // Define pagination
        Pageable pageable = PageRequest.of(page - 1, size);

        // Get the current date
        LocalDate today = LocalDate.now();

        // Fetch appointments for today and the specific doctor
        Page<Appointment> appointments = appointmentRepository.findByDoctorIDAndAppointmentDate(doctorID, today, pageable);

        // Map to pagination DTO
        return mapToPaginationDTO(appointments);
    }

    @Transactional
    public PaginationDTO<Appointment> searchAppointmentsForDoctor(Long doctorID, String search, LocalDate date, int page, int size) {
        // Define pagination
        Pageable pageable = PageRequest.of(page - 1, size);

        // Fetch appointments based on the doctor ID, date, and search criteria
        Page<Appointment> appointments = appointmentRepository.searchAppointmentsByDoctorIDAndPatientDetailsAndDate(doctorID, date, search, pageable);

        // Map to pagination DTO
        return mapToPaginationDTO(appointments);
    }





    private PaginationDTO<Appointment> mapToPaginationDTO(Page<Appointment> page) {
        PaginationDTO<Appointment> paginationDTO = new PaginationDTO<>();
        paginationDTO.setTotalElements(page.getTotalElements());
        paginationDTO.setTotalPages(page.getTotalPages());
        paginationDTO.setSize(page.getSize());
        paginationDTO.setNumber(page.getNumber() + 1);
        paginationDTO.setNumberOfElements(page.getNumberOfElements());
        paginationDTO.setContent(page.getContent());
        return paginationDTO;
    }
}
