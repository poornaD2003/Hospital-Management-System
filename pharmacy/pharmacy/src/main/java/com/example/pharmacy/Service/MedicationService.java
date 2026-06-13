package com.example.pharmacy.Service;

import com.example.pharmacy.model.Medication;
import java.util.List;
import java.util.Optional;

public interface MedicationService {

    Medication saveMedication(Medication medication);

    List<Medication> getAllMedications();

    Optional<Medication> getMedicationById(Long id);

    Medication updateMedication(Medication medication, Long id);

    void deleteMedication(Long id);
    Medication issueMedication(Long medicineId, int quantityIssued);
}