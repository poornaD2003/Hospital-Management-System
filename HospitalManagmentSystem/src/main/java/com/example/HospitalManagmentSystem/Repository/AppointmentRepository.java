package com.example.HospitalManagmentSystem.Repository;



import com.example.HospitalManagmentSystem.Model.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

}
