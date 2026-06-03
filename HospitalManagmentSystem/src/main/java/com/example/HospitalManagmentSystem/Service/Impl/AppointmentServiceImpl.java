package com.example.HospitalManagmentSystem.Service.Impl;


import com.example.HospitalManagmentSystem.Model.Appointment;
import com.example.HospitalManagmentSystem.Repository.AppointmentRepository;
import com.example.HospitalManagmentSystem.Service.AppointmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class AppointmentServiceImpl
        implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public Appointment saveAppointment(Appointment appointment) {

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {

        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(long id) {

        Optional<Appointment> appointment =
                appointmentRepository.findById(id);

        if(appointment.isPresent()){

            return appointment.get();

        }else{

            throw new RuntimeException();
        }
    }

    @Override
    public Appointment updateAppointment(
            Appointment appointment,
            long id) {

        Appointment existingAppointment =
                appointmentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException());

        existingAppointment.setPatientName(
                appointment.getPatientName());

        existingAppointment.setDoctorName(
                appointment.getDoctorName());

        existingAppointment.setSpecialization(
                appointment.getSpecialization());

        existingAppointment.setAppointmentDate(
                appointment.getAppointmentDate());

        existingAppointment.setAppointmentTime(
                appointment.getAppointmentTime());

        appointmentRepository.save(existingAppointment);

        return existingAppointment;
    }

    @Override
    public void deleteAppointment(long id) {

        appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException());

        appointmentRepository.deleteById(id);
    }
}