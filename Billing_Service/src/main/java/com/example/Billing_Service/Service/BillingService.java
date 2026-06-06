package com.example.Billing_Service.Service;


import com.example.Billing_Service.Model.Billing;

import java.util.List;

public interface BillingService {

    Billing saveBilling(Billing billing);

    List<Billing> getAllBilling();

    Billing getBillingById(long id);

    Billing updateBilling(Billing billing, long id);

    void deleteBilling(long id);
}