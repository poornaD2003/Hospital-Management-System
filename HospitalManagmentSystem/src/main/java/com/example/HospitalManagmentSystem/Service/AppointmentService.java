package com.example.HospitalManagmentSystem.Service;


import com.example.HospitalManagmentSystem.Model.Appointment;

import java.util.List;

public interface AppointmentService {

    Appointment saveAppointment(Appointment appointment);

    List<Appointment> getAllAppointments();

    Appointment getAppointmentById(long id);

    Appointment updateAppointment(Appointment appointment, long id);

    void deleteAppointment(long id);
}
