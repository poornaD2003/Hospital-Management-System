package com.sms.patient.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sms.patient.model.Patient;
import com.sms.patient.repository.PatientRepository;
import com.sms.patient.service.PatientService;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Patient register(Patient patient) {
        // hash password before saving
        if (patient.getPassword() != null) {
            patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        }
        return patientRepository.save(patient);
    }

    @Override
    public Optional<Patient> login(String email, String password) {
        Optional<Patient> p = patientRepository.findByEmail(email);
        if (p.isPresent() && password != null && passwordEncoder.matches(password, p.get().getPassword())) {
            return p;
        }
        return Optional.empty();
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return patientRepository.findByEmail(email);
    }
}
