package com.sms.patient.service;

import java.util.List;
import java.util.Optional;

import com.sms.patient.model.Patient;

public interface PatientService {
    Patient register(Patient patient);
    Optional<Patient> login(String email, String password);
    Optional<Patient> findById(Long id);
    List<Patient> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    Optional<Patient> update(Long id, Patient patient);
}
