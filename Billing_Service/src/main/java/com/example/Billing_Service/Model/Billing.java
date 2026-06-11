package com.example.Billing_Service.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "billing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_name", nullable = false)
    private String patientName;

    @Column(name = "amount", nullable = false)
    private Double amount; // This will hold the calculated grand total

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    // Stores one-by-one added medical services
    @ElementCollection
    @CollectionTable(name = "billing_services", joinColumns = @JoinColumn(name = "billing_id"))
    private List<BillingServiceItem> serviceItems;

    // Stores one-by-one added medications with their quantity and unit price
    @ElementCollection
    @CollectionTable(name = "billing_medicines", joinColumns = @JoinColumn(name = "billing_id"))
    private List<BillingMedicineItem> medicineItems;
}