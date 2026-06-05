package com.example.pharmacy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "prescription_issues")
public class PrescriptionIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "patient_id", nullable = false)
    private long patientId;

    @Column(name = "medicine_id", nullable = false)
    private long medicineId;

    @Column(name = "medicine_name")
    private String medicineName;

    @Column(name = "quantity_issued", nullable = false)
    private int quantityIssued;

    @Column(name = "total_bill", nullable = false)
    private double totalBill;

    public PrescriptionIssue() {}

    public PrescriptionIssue(long patientId, long medicineId, String medicineName, int quantityIssued, double totalBill) {
        this.patientId = patientId;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantityIssued = quantityIssued;
        this.totalBill = totalBill;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getPatientId() { return patientId; }
    public void setPatientId(long patientId) { this.patientId = patientId; }
    public long getMedicineId() { return medicineId; }
    public void setMedicineId(long medicineId) { this.medicineId = medicineId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public int getQuantityIssued() { return quantityIssued; }
    public void setQuantityIssued(int quantityIssued) { this.quantityIssued = quantityIssued; }
    public double getTotalBill() { return totalBill; }
    public void setTotalBill(double totalBill) { this.totalBill = totalBill; }
}