package com.example.HospitalManagmentSystem.Controller;


import com.example.HospitalManagmentSystem.Model.Appointment;
import com.example.HospitalManagmentSystem.Service.AppointmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/appointments")



public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping

    public ResponseEntity<Appointment>
    saveAppointment(@RequestBody Appointment appointment){

        return new ResponseEntity<Appointment>(
                appointmentService.saveAppointment(appointment),
                HttpStatus.CREATED
        );
    }

    @GetMapping

    public List<Appointment> getAllAppointments(){

        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")

    public ResponseEntity<Appointment>
    getAppointmentById(@PathVariable("id") long appointmentID){

        return new ResponseEntity<Appointment>(
                appointmentService.getAppointmentById(appointmentID),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")

    public ResponseEntity<Appointment>
    updateAppointment(
            @PathVariable("id") long id,

            @RequestBody Appointment appointment){

        return new ResponseEntity<Appointment>(
                appointmentService.updateAppointment(appointment,id),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<String>
    deleteAppointment(@PathVariable("id") long id){

        appointmentService.deleteAppointment(id);

        return new ResponseEntity<String>(
                "Appointment Deleted Successfully",
                HttpStatus.OK
        );
    }
}
