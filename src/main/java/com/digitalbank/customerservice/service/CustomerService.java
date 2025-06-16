package com.digitalbank.customerservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitalbank.customerservice.exception.ResourceNotFoundException;
import com.digitalbank.customerservice.model.Customer;
import com.digitalbank.customerservice.model.KycStatus;
import com.digitalbank.customerservice.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        customer.setKycStatus(KycStatus.PENDING);
        return customerRepository.save(customer);
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    public Customer updateCustomer(Long id, Customer updated) {
        Customer existing = getCustomer(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        return customerRepository.save(existing);
    }
}
