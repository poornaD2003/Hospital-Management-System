package com.example.HospitalManagmentSystem.Service.Impl;


import com.example.HospitalManagmentSystem.Model.Billing;
import com.example.HospitalManagmentSystem.Repository.BillingRepository;
import com.example.HospitalManagmentSystem.Service.BillingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class BillingServiceImpl
        implements BillingService {

    @Autowired
    private BillingRepository billingRepository;

    @Override
    public Billing saveBilling(Billing billing) {

        return billingRepository.save(billing);
    }

    @Override
    public List<Billing> getAllBilling() {

        return billingRepository.findAll();
    }

    @Override
    public Billing getBillingById(long id) {

        Optional<Billing> billing =
                billingRepository.findById(id);

        if(billing.isPresent()){

            return billing.get();

        }else{

            throw new RuntimeException();
        }
    }

    @Override
    public Billing updateBilling(
            Billing billing,
            long id) {

        Billing existingBilling =
                billingRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException());

        existingBilling.setPatientName(
                billing.getPatientName());

        existingBilling.setServiceName(
                billing.getServiceName());

        existingBilling.setAmount(
                billing.getAmount());

        existingBilling.setPaymentMethod(
                billing.getPaymentMethod());

        existingBilling.setPaymentStatus(
                billing.getPaymentStatus());

        billingRepository.save(existingBilling);

        return existingBilling;
    }

    @Override
    public void deleteBilling(long id) {

        billingRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException());

        billingRepository.deleteById(id);
    }
}