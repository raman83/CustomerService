package com.digitalbank.customerservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbank.customerservice.model.Customer;
import com.digitalbank.customerservice.service.CustomerService;

@RestController
@RequestMapping("/api/v1")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/customers")
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> get(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomer(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
        return ResponseEntity.ok(customerService.updateCustomer(id, customer));
    }
}
