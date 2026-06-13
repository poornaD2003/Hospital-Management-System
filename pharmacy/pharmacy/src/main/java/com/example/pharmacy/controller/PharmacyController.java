package com.example.pharmacy.controller;

import com.example.pharmacy.model.Medication;
import com.example.pharmacy.Service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")

public class PharmacyController {

    @Autowired
    private MedicationService medicationService;

    @PostMapping
    public ResponseEntity<Medication> addMedication(@RequestBody Medication medication) {
        return new ResponseEntity<>(medicationService.saveMedication(medication), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Medication> getAllMedications() {
        return medicationService.getAllMedications();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medication> getMedicationById(@PathVariable("id") Long id) {
        return medicationService.getMedicationById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Medication> updateMedication(@PathVariable("id") Long id, @RequestBody Medication medicationDetails) {
        return ResponseEntity.ok(medicationService.updateMedication(medicationDetails, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedication(@PathVariable("id") Long id) {
        medicationService.deleteMedication(id);
        return ResponseEntity.ok("Medication Deleted Successfully");
    }

}

