package com.sms.patient.service;

import java.util.Optional;

import com.sms.patient.model.Patient;

public interface PatientService {
    Patient register(Patient patient);
    Optional<Patient> login(String email, String password);
    Optional<Patient> findById(Long id);
    Optional<Patient> findByEmail(String email);
}
