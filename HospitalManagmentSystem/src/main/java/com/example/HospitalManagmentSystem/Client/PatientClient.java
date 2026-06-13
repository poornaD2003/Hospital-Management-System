package com.example.HospitalManagmentSystem.Client;

import com.example.HospitalManagmentSystem.DTO.patientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "http://localhost:8085/api/patients")
public interface PatientClient {

    @GetMapping("/{id}")
    patientDTO getPatientById(@PathVariable("id") Long id);
}