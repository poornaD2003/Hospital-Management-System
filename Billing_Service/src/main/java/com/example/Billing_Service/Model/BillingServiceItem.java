package com.example.Billing_Service.Model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class BillingServiceItem {
    private String serviceName;
    private Double serviceFee;
}