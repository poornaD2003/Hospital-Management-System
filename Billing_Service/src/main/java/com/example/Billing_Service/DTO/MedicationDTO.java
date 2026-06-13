package com.example.Billing_Service.DTO;
import lombok.Data;

@Data
public class MedicationDTO {
    private Long id;
    private String medicineName;
    private int stockQuantity;
    private Double pricePerUnit;
}