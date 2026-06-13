package com.example.Billing_Service.Client;

import com.example.Billing_Service.DTO.PatientDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "http://localhost:8085/api/patients")
public interface PatientClient {
    @GetMapping("/{id}")
    PatientDTO getPatientById(@PathVariable("id") Long id);
}