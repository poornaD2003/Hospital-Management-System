package com.example.HospitalManagmentSystem.Service;


import com.example.HospitalManagmentSystem.Model.Billing;

import java.util.List;

public interface BillingService {

    Billing saveBilling(Billing billing);

    List<Billing> getAllBilling();

    Billing getBillingById(long id);

    Billing updateBilling(Billing billing, long id);

    void deleteBilling(long id);
}