package com.example.Billing_Service.Model;


import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;
}