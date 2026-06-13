package com.example.Billing_Service.Service.Impl;

import com.example.Billing_Service.Client.PatientClient;
import com.example.Billing_Service.Client.PharmacyClient;
import com.example.Billing_Service.DTO.MedicationDTO;
import com.example.Billing_Service.DTO.PatientDTO;
import com.example.Billing_Service.Model.Billing;
import com.example.Billing_Service.Model.BillingMedicineItem;
import com.example.Billing_Service.Model.BillingServiceItem;
import com.example.Billing_Service.Repository.BillingRepository;
import com.example.Billing_Service.Service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private PatientClient patientClient;

    @Autowired
    private PharmacyClient pharmacyClient;

    @Override
    public Billing saveBilling(Billing billing) {
        double totalAmount = calculateGrandTotal(billing);
        billing.setAmount(totalAmount);

        return billingRepository.save(billing);
    }

    @Override
    public List<Billing> getAllBilling() {
        List<Billing> billings = billingRepository.findAll();
        for (Billing b : billings) {
            try {
                if (b.getPatientId() != null) {
                    PatientDTO p = patientClient.getPatientById(b.getPatientId());
                    b.setPatientName(p.getName());
                }
            } catch (Exception e) {
                b.setPatientName("Unknown Patient");
            }

            if (b.getMedicineItems() != null) {
                for (BillingMedicineItem item : b.getMedicineItems()) {
                    try {
                        MedicationDTO med = pharmacyClient.getMedicationById(item.getMedicineId());
                        if (med != null) {
                            item.setMedicineName(med.getMedicineName());
                        }
                    } catch (Exception e) {
                        item.setMedicineName("Unknown Medicine");
                    }
                }
            }
        }
        return billings;
    }

    @Override
    public Billing getBillingById(long id) {
        Billing b = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing record not found with ID: " + id));

        try {
            if (b.getPatientId() != null) {
                PatientDTO p = patientClient.getPatientById(b.getPatientId());
                b.setPatientName(p.getName());
            }
        } catch (Exception e) {
            b.setPatientName("Unknown Patient");
        }

        if (b.getMedicineItems() != null) {
            for (BillingMedicineItem item : b.getMedicineItems()) {
                try {
                    MedicationDTO med = pharmacyClient.getMedicationById(item.getMedicineId());
                    if (med != null) {
                        item.setMedicineName(med.getMedicineName());
                    }
                } catch (Exception e) {
                    item.setMedicineName("Unknown Medicine");
                }
            }
        }
        return b;
    }

    @Override
    public Billing updateBilling(Billing billing, long id) {
        Billing existingBilling = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing record not found with ID: " + id));

        existingBilling.setPatientId(billing.getPatientId());
        existingBilling.setPaymentMethod(billing.getPaymentMethod());
        existingBilling.setPaymentStatus(billing.getPaymentStatus());

        if (existingBilling.getServiceItems() != null) {
            existingBilling.getServiceItems().clear();
            if (billing.getServiceItems() != null) {
                existingBilling.getServiceItems().addAll(billing.getServiceItems());
            }
        }


        if (existingBilling.getMedicineItems() != null) {
            existingBilling.getMedicineItems().clear();
            if (billing.getMedicineItems() != null) {
                existingBilling.getMedicineItems().addAll(billing.getMedicineItems());
            }
        }

        double recalculatedTotal = calculateGrandTotal(existingBilling);
        existingBilling.setAmount(recalculatedTotal);

        return billingRepository.save(existingBilling);
    }

    @Override
    public void deleteBilling(long id) {
        billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing record not found with ID: " + id));
        billingRepository.deleteById(id);
    }


    private double calculateGrandTotal(Billing billing) {
        double totalAmount = 0.0;

        if (billing.getServiceItems() != null) {
            for (BillingServiceItem item : billing.getServiceItems()) {
                if (item.getServiceFee() != null) {
                    totalAmount += item.getServiceFee();
                }
            }
        }

        if (billing.getMedicineItems() != null) {
            for (BillingMedicineItem item : billing.getMedicineItems()) {
                try {
                    MedicationDTO med = pharmacyClient.getMedicationById(item.getMedicineId());

                    if (med != null) {
                        item.setMedicineName(med.getMedicineName());
                        item.setUnitPrice(med.getPricePerUnit());

                        totalAmount += (med.getPricePerUnit() * item.getQuantity());
                    } else {
                        System.out.println("⚠️ Medicine NOT FOUND in Pharmacy for ID: " + item.getMedicineId());
                    }
                } catch (Exception e) {
                    System.err.println("❌ Feign Client Error for Medicine ID " + item.getMedicineId() + ": " + e.getMessage());
                    e.printStackTrace();

                    if (item.getUnitPrice() != null) {
                        totalAmount += (item.getUnitPrice() * item.getQuantity());
                    }
                }
            }
        }

        return totalAmount;
    }
}