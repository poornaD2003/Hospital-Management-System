package com.example.Billing_Service.Controller;


import com.example.Billing_Service.Model.Billing;
import com.example.Billing_Service.Service.BillingService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/billing")


public class BillingController {

    @Autowired
    private BillingService billingService;

    @PostMapping

    public ResponseEntity<Billing>
    saveBilling(@RequestBody Billing billing){

        return new ResponseEntity<Billing>(
                billingService.saveBilling(billing),
                HttpStatus.CREATED
        );
    }

    @GetMapping

    public List<Billing> getAllBilling(){

        return billingService.getAllBilling();
    }

    @GetMapping("/{id}")

    public ResponseEntity<Billing>
    getBillingById(@PathVariable("id") long billingID){

        return new ResponseEntity<Billing>(
                billingService.getBillingById(billingID),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")

    public ResponseEntity<Billing>
    updateBilling(
            @PathVariable("id") long id,

            @RequestBody Billing billing){

        return new ResponseEntity<Billing>(
                billingService.updateBilling(billing,id),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<String>
    deleteBilling(@PathVariable("id") long id){

        billingService.deleteBilling(id);

        return new ResponseEntity<String>(
                "Billing Deleted Successfully",
                HttpStatus.OK
        );
    }
}