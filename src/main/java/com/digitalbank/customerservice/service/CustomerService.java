package com.digitalbank.customerservice.service;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.digitalbank.customerservice.client.AuthServiceClient;
import com.digitalbank.customerservice.dto.CustomerRegistrationRequest;
import com.digitalbank.customerservice.dto.CustomerRequest;
import com.digitalbank.customerservice.dto.CustomerResponse;
import com.digitalbank.customerservice.dto.UpdateCustomerRequest;
import com.digitalbank.customerservice.exception.ResourceNotFoundException;
import com.digitalbank.customerservice.kafka.CustomerEventProducer;
import com.digitalbank.customerservice.mapper.CustomerMapper;
import com.digitalbank.customerservice.model.Customer;
import com.digitalbank.customerservice.model.KycStatus;
import com.digitalbank.customerservice.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CustomerService {

    private final AuthServiceClient authServiceClient;
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final CustomerEventProducer eventProducer;

    public CustomerResponse create(CustomerRequest request) {
        Customer customer = mapper.toEntity(request);
        Customer saved = repository.save(customer);
        eventProducer.publishCustomerCreatedEvent(mapper.toEvent(saved));
        return mapper.toResponse(saved);
    }
    
    public CustomerResponse getByExternalId(String externalId) {
        var customer = repository.findByExternalId(externalId)
            .orElseThrow(() -> new NoSuchElementException("Customer not found with externalId: " + externalId));
        return mapper.toResponse(customer);
    }
    
    public boolean exists(String externalId) {
        return repository.findByExternalId(externalId).isPresent();
            
    }
    
   
    
    public CustomerResponse updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));

        customer.setFirstName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        customer = repository.save(customer);
        return mapper.toResponse(customer);

    }

    public void updateKycStatus(Long id, String kycStatus) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));

        
        if (kycStatus.equalsIgnoreCase("VERIFIED")) {
            // Trigger auth service registration
            CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer.getEmail(),"default-password", customer.getExternalId());
            authServiceClient.registerCustomer(request);
        }
        
        customer.setKycStatus(KycStatus.VERIFIED);
        repository.save(customer);
    }


    public Optional<CustomerResponse> getById(Long id) {
        return repository.findById(id).map(mapper::toResponse);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }
}