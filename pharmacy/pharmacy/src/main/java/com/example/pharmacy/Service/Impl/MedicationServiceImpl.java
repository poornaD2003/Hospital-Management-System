package com.example.pharmacy.Service.Impl;

import com.example.pharmacy.model.Medication;
import com.example.pharmacy.repository.PharmacyRepository;
import com.example.pharmacy.Service.MedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MedicationServiceImpl implements MedicationService {

    @Autowired
    private PharmacyRepository pharmacyRepository; // ඔයාගේ පරණ Repository එක Inject කළා

    @Override
    public Medication saveMedication(Medication medication) {
        return pharmacyRepository.save(medication);
    }

    @Override
    public List<Medication> getAllMedications() {
        return pharmacyRepository.findAll();
    }

    @Override
    public Optional<Medication> getMedicationById(Long id) {
        return pharmacyRepository.findById(id);
    }

    @Override
    public Medication updateMedication(Medication medicationDetails, Long id) {
        Medication existingMedication = pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication not found with id: " + id));

        existingMedication.setMedicineName(medicationDetails.getMedicineName());
        existingMedication.setStockQuantity(medicationDetails.getStockQuantity());
        existingMedication.setPricePerUnit(medicationDetails.getPricePerUnit());

        return pharmacyRepository.save(existingMedication);
    }

    @Override
    public void deleteMedication(Long id) {
        Medication medication = pharmacyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medication not found with id: " + id));
        pharmacyRepository.delete(medication);
    }
    @Override
    @Transactional
    public Medication issueMedication(Long medicineId, int quantityIssued) {
        // 1. බෙහෙත ඩේටාබේස් එකේ තියෙනවද බලනවා
        Medication medication = pharmacyRepository.findById(medicineId)
                .orElseThrow(() -> new RuntimeException("Error: Selected Medicine ID does not exist!"));

        // 2. ඉල්ලන ප්‍රමාණයට වඩා ස්ටොක් තියෙනවද බලනවා
        if (medication.getStockQuantity() < quantityIssued) {
            throw new RuntimeException("Error: Insufficient stock! Only " + medication.getStockQuantity() + " units left.");
        }

        // 3. ස්ටොක් අඩු කරලා සේව් කරනවා
        int newStock = medication.getStockQuantity() - quantityIssued;
        medication.setStockQuantity(newStock);
        return pharmacyRepository.save(medication);
    }
}