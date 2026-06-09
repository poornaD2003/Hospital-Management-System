package com.example.Billing_Service.Repository;


import com.example.Billing_Service.Model.Billing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository
        extends JpaRepository<Billing, Long> {

}