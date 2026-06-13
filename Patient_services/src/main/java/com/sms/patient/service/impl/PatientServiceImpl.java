package com.sms.patient.service.impl;

import java.util.List;
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
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return patientRepository.existsById(id);
    }

    @Override
    public Optional<Patient> update(Long id, Patient patient) {
        return patientRepository.findById(id).map(existingPatient -> {
            existingPatient.setName(patient.getName());
            existingPatient.setEmail(patient.getEmail());
            if(patient.getPassword() != null && !patient.getPassword().isEmpty()) {
                existingPatient.setPassword(passwordEncoder.encode(patient.getPassword()));
            }
            existingPatient.setAge(patient.getAge());
            existingPatient.setPhone(patient.getPhone());
            return patientRepository.save(existingPatient);
        });
    }


}
