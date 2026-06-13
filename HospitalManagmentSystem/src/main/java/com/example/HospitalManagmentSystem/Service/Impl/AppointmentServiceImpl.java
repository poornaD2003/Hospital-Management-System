package com.example.HospitalManagmentSystem.Service.Impl;

import com.example.HospitalManagmentSystem.Client.PatientClient;
import com.example.HospitalManagmentSystem.DTO.patientDTO;
import com.example.HospitalManagmentSystem.Model.Appointment;
import com.example.HospitalManagmentSystem.Repository.AppointmentRepository;
import com.example.HospitalManagmentSystem.Service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientClient patientClient;

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        try {
            patientDTO patient = patientClient.getPatientById(appointment.getPatientId());
            if (patient == null) {
                throw new RuntimeException("Patient not found!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Validation Failed: Patient Service Error");
        }
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        for (Appointment app : appointments) {
            try {
                patientDTO p = patientClient.getPatientById(app.getPatientId());
                app.setPatientName(p.getName());
            } catch (Exception e) {
                app.setPatientName("Unknown Patient");
            }
        }
        return appointments;
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        try {
            patientDTO p = patientClient.getPatientById(appointment.getPatientId());
            appointment.setPatientName(p.getName());
        } catch (Exception e) {
            appointment.setPatientName("Unknown Patient");
        }
        return appointment;
    }

    @Override
    public Appointment updateAppointment(Appointment appointment, long id) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        existingAppointment.setPatientId(appointment.getPatientId());
        existingAppointment.setDoctorName(appointment.getDoctorName());
        existingAppointment.setSpecialization(appointment.getSpecialization());
        existingAppointment.setAppointmentDate(appointment.getAppointmentDate());
        existingAppointment.setAppointmentTime(appointment.getAppointmentTime());

        return appointmentRepository.save(existingAppointment);
    }

    @Override
    public void deleteAppointment(long id) {
        appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointmentRepository.deleteById(id);
    }
}