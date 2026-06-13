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
        // ඩේටාබේස් එකට දාන්න කලින් මිල ගණන් සහ නම් Feign Client එකෙන් අරන් Grand Total එක හදනවා
        double totalAmount = calculateGrandTotal(billing);
        billing.setAmount(totalAmount);

        return billingRepository.save(billing);
    }

    @Override
    public List<Billing> getAllBilling() { // Interface එකේ නමට අනුව getAllBillings ලෙස තැබුවා
        List<Billing> billings = billingRepository.findAll();
        for (Billing b : billings) {
            // Patient Service එකෙන් නම අරන් Dynamic සෙට් කරනවා
            try {
                if (b.getPatientId() != null) {
                    PatientDTO p = patientClient.getPatientById(b.getPatientId());
                    b.setPatientName(p.getName());
                }
            } catch (Exception e) {
                b.setPatientName("Unknown Patient");
            }

            // බෙහෙත් වල නම් ටිකත් Pharmacy එකෙන් අරන් Dynamic සෙට් කරනවා
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

        // තනි රෙකෝඩ් එකක් ගනිද්දීත් Patient Name එක Fetch කරනවා
        try {
            if (b.getPatientId() != null) {
                PatientDTO p = patientClient.getPatientById(b.getPatientId());
                b.setPatientName(p.getName());
            }
        } catch (Exception e) {
            b.setPatientName("Unknown Patient");
        }

        // බෙහෙත් වල විස්තර Fetch කරනවා
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

        // මූලික විස්තර ටික අප්ඩේට් කරනවා
        existingBilling.setPatientId(billing.getPatientId());
        existingBilling.setPaymentMethod(billing.getPaymentMethod());
        existingBilling.setPaymentStatus(billing.getPaymentStatus());

        // 1. Hospital Services ලිස්ට් එක ආරක්ෂිතව අප්ඩේට් කිරීම
        if (existingBilling.getServiceItems() != null) {
            existingBilling.getServiceItems().clear();
            if (billing.getServiceItems() != null) {
                existingBilling.getServiceItems().addAll(billing.getServiceItems());
            }
        }

        // 2. 💡 වැදගත්ම කොටස: OneToMany ලිස්ට් එක කෙලින්ම set කරන්නේ නැතුව,
        // පරණ ලිස්ට් එක clear කරලා අලුත් දත්ත ටික add කරනවා. (මෙවිට Hibernate Error එක එන්නේ නැත)
        if (existingBilling.getMedicineItems() != null) {
            existingBilling.getMedicineItems().clear();
            if (billing.getMedicineItems() != null) {
                existingBilling.getMedicineItems().addAll(billing.getMedicineItems());
            }
        }

        // අලුත් අයිතමයන්ට අනුව මිල ගණන් සහ Grand Total එක නැවත ගණනය කරනවා
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
     * Helper Method: වෛද්‍ය සේවා ගාස්තු + Pharmacy එකෙන් ගන්නා බෙහෙත් වල (මිල * ප්‍රමාණය)
     */
    private double calculateGrandTotal(Billing billing) {
        double totalAmount = 0.0;

        // 1. Hospital Services එකතුව බලමු
        if (billing.getServiceItems() != null) {
            for (BillingServiceItem item : billing.getServiceItems()) {
                if (item.getServiceFee() != null) {
                    totalAmount += item.getServiceFee();
                }
            }
        }

        // 2. Pharmacy සර්විස් එකෙන් දත්ත ගෙන ලිස්ට් එකට දමා සේව් කරමු
        if (billing.getMedicineItems() != null) {
            for (BillingMedicineItem item : billing.getMedicineItems()) {
                try {
                    // Pharmacy Service එකට කතා කරනවා
                    MedicationDTO med = pharmacyClient.getMedicationById(item.getMedicineId());

                    if (med != null) {
                        // 💡 ලැබෙන සැබෑ දත්ත ටික Database එකට යන Object එකටම සෙට් කරනවා
                        item.setMedicineName(med.getMedicineName());
                        item.setUnitPrice(med.getPricePerUnit());

                        totalAmount += (med.getPricePerUnit() * item.getQuantity());
                    } else {
                        System.out.println("⚠️ Medicine NOT FOUND in Pharmacy for ID: " + item.getMedicineId());
                    }
                } catch (Exception e) {
                    // 💡 වැරැද්දක් ආවොත් console එකේ බලාගන්න මේක දැම්මා
                    System.err.println("❌ Feign Client Error for Medicine ID " + item.getMedicineId() + ": " + e.getMessage());
                    e.printStackTrace();

                    // සර්විස් එක ඩවුන් නම්, දැනට ෆ්‍රන්ට්එන්ඩ් එකෙන් ආපු මිලක් තිබේ නම් එයින් හදයි
                    if (item.getUnitPrice() != null) {
                        totalAmount += (item.getUnitPrice() * item.getQuantity());
                    }
                }
            }
        }

        return totalAmount;
    }
}