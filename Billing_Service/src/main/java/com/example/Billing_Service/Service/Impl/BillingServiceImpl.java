package com.example.Billing_Service.Service.Impl;

import com.example.Billing_Service.Model.Billing;
import com.example.Billing_Service.Model.BillingServiceItem;
import com.example.Billing_Service.Model.BillingMedicineItem;
import com.example.Billing_Service.Repository.BillingRepository;
import com.example.Billing_Service.Service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillingServiceImpl implements BillingService {

    @Autowired
    private BillingRepository billingRepository;

    @Override
    public Billing saveBilling(Billing billing) {
        // Calculate the total fee before saving to the database
        double calculatedTotal = calculateGrandTotal(billing);
        billing.setAmount(calculatedTotal);

        return billingRepository.save(billing);
    }

    @Override
    public List<Billing> getAllBilling() {
        return billingRepository.findAll();
    }

    @Override
    public Billing getBillingById(long id) {
        Optional<Billing> billing = billingRepository.findById(id);
        if (billing.isPresent()) {
            return billing.get();
        } else {
            throw new RuntimeException("Billing record not found with ID: " + id);
        }
    }

    @Override
    public Billing updateBilling(Billing billing, long id) {
        Billing existingBilling = billingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billing record not found with ID: " + id));

        // Update core info fields
        existingBilling.setPatientName(billing.getPatientName());
        existingBilling.setPaymentMethod(billing.getPaymentMethod());
        existingBilling.setPaymentStatus(billing.getPaymentStatus());

        // Replace items lists
        existingBilling.setServiceItems(billing.getServiceItems());
        existingBilling.setMedicineItems(billing.getMedicineItems());

        // Recalculate totals
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

    /**
     * Logic: Sum of Service Fees + Sum of (Medicine Unit Price * Quantity)
     */
    private double calculateGrandTotal(Billing billing) {
        double totalServiceFee = 0.0;
        double totalMedicineCost = 0.0;

        // 1. Calculate services fee sum
        if (billing.getServiceItems() != null) {
            for (BillingServiceItem service : billing.getServiceItems()) {
                if (service.getServiceFee() != null) { // 👈 Null Check
                    totalServiceFee += service.getServiceFee();
                }
            }
        }

        // 2. Calculate medicine cost: unit price * quantity
        if (billing.getMedicineItems() != null) {
            for (BillingMedicineItem medicine : billing.getMedicineItems()) {
                if (medicine.getUnitPrice() != null) { // 👈 Null Check
                    totalMedicineCost += (medicine.getUnitPrice() * medicine.getQuantity());
                }
            }
        }

        return totalServiceFee + totalMedicineCost;
    }
}