package com.example.HospitalManagmentSystem.Repository;


import com.example.HospitalManagmentSystem.Model.Billing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BillingRepository
        extends JpaRepository<Billing, Long> {

}