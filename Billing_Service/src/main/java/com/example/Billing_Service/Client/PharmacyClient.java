package com.example.Billing_Service.Client;

import com.example.Billing_Service.DTO.MedicationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pharmacy-service", url = "http://localhost:8083/api/pharmacy")
public interface PharmacyClient {
    @GetMapping("/{id}")
    MedicationDTO getMedicationById(@PathVariable("id") Long id);
}