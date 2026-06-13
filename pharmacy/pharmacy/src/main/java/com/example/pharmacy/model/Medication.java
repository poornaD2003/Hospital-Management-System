package com.example.pharmacy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medications")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "price_per_unit", nullable = false)
    private double pricePerUnit;

    // Default Constructor
    public Medication() {}

    // Full Constructor
    public Medication(Long id, String medicineName, int stockQuantity, double pricePerUnit) {
        this.id = id;
        this.medicineName = medicineName;
        this.stockQuantity = stockQuantity;
        this.pricePerUnit = pricePerUnit;
    }

    // Explicit Getters and Setters (මෙමඟින් වැරැද්ද සම්පූර්ණයෙන්ම නිවැරදි වේ)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}