package com.example.pharmacy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "medications")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    @Column(name = "price_per_unit", nullable = false)
    private double pricePerUnit;

    // Default Constructor
    public Medication() {}

    // Special Form-Data Constructor (Handles the POST null-ID scenario seamlessly)
    public Medication(String medicineName, int stockQuantity, double pricePerUnit) {
        this.medicineName = medicineName;
        this.stockQuantity = stockQuantity;
        this.pricePerUnit = pricePerUnit;
    }

    // Complete Constructor
    public Medication(long id, String medicineName, int stockQuantity, double pricePerUnit) {
        this.id = id;
        this.medicineName = medicineName;
        this.stockQuantity = stockQuantity;
        this.pricePerUnit = pricePerUnit;
    }

    // Explicit Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }
}