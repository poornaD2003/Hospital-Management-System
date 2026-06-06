package com.example.Billing_Service.Model;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "prescription_items", schema = "hospital_db")
@Data
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_id")
    private Long medicineId; // Foreign key referencing medications.id in the other DB

    private int quantity;


}
