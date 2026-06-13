package com.example.Billing_Service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // 💡 Embeddable වෙනුවට Entity එකක් කළා
@Table(name = "billing_medicines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingMedicineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key එකක් එකතු කළා

    @Column(name = "medicine_id", nullable = false)
    private Long medicineId;

    @Column(name = "medicine_name")
    private String medicineName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price")
    private Double unitPrice;
}