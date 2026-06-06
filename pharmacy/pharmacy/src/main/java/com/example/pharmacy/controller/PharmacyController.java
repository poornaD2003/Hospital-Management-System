package com.example.pharmacy.controller;

import com.example.pharmacy.model.Medication;
//import com.example.pharmacy.model.PrescriptionIssue; // Added this missing import
import com.example.pharmacy.repository.PharmacyRepository;
//import com.example.pharmacy.repository.PrescriptionIssueRepository; // Added this missing import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pharmacy")
public class PharmacyController {

    @Autowired
    private PharmacyRepository pharmacyRepository;

    // @Autowired
    //private PrescriptionIssueRepository issueRepository; // Grouped nicely at the top

    // Create / Add New Medication Stock
    @PostMapping
    public ResponseEntity<Medication> addMedication(@RequestBody Medication medication) {
        return new ResponseEntity<>(pharmacyRepository.save(medication), HttpStatus.CREATED);
    }

    // Get All Medications in Inventory
    @GetMapping
    public List<Medication> getAllMedications() {
        return pharmacyRepository.findAll();
    }

    // Get Single Medication Details by ID
    @GetMapping("/{id}")
    public ResponseEntity<Medication> getMedicationById(@PathVariable("id") long id) {
        Medication medication = pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication record not found."));
        return ResponseEntity.ok(medication);
    }

    // Update Medication Details (Stock adjustments or price updates)
    @PutMapping("/{id}")
    public ResponseEntity<Medication> updateMedication(@PathVariable("id") long id, @RequestBody Medication details) {
        Medication existingMedication = pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication record not found."));

        existingMedication.setMedicineName(details.getMedicineName());
        existingMedication.setStockQuantity(details.getStockQuantity());
        existingMedication.setPricePerUnit(details.getPricePerUnit());

        return ResponseEntity.ok(pharmacyRepository.save(existingMedication));
    }

    // Delete Medication Record from stock
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedication(@PathVariable("id") long id) {
        pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication record not found."));
        pharmacyRepository.deleteById(id);
        return new ResponseEntity<>("Medication removed from database successfully.", HttpStatus.OK);
    }
}
  /*  // Medicine Dispensing & Billing Endpoint
    @PostMapping("/issue")
    public ResponseEntity<?> issueMedication(@RequestBody PrescriptionIssue issueRequest) {
        // Find if the medicine exists in stock
        Medication medication = pharmacyRepository.findById(issueRequest.getMedicineId())
                .orElse(null);

        if (medication == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Error: Selected Medicine ID does not exist!\"}");
        }

        // Check if enough stock units are available
        if (medication.getStockQuantity() < issueRequest.getQuantityIssued()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"message\": \"Error: Insufficient stock! Only " + medication.getStockQuantity() + " units left.\"}");
        }

        // Deduct stock from inventory
        medication.setStockQuantity(medication.getStockQuantity() - issueRequest.getQuantityIssued());
        pharmacyRepository.save(medication);

        // Calculate total amount bill cost
        double totalCost = medication.getPricePerUnit() * issueRequest.getQuantityIssued();

        // Save the sale transaction history record securely
        issueRequest.setMedicineName(medication.getMedicineName());
        issueRequest.setTotalBill(totalCost);
        PrescriptionIssue savedIssue = issueRepository.save(issueRequest);

        return new ResponseEntity<>(savedIssue, HttpStatus.CREATED);
    }
}*/