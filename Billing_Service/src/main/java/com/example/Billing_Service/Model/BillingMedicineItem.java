package com.example.Billing_Service.Model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class BillingMedicineItem {
    private String medicineName;
    private int quantity;
    private Double unitPrice; // Stored at the time of calculation
}