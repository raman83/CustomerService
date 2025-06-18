package com.digitalbank.customerservice.mapper;


import org.springframework.stereotype.Component;

import com.digitalbank.customerservice.dto.CustomerRequest;
import com.digitalbank.customerservice.dto.CustomerResponse;
import com.digitalbank.customerservice.kafka.CustomerCreatedEvent;
import com.digitalbank.customerservice.model.Customer;
import com.digitalbank.customerservice.model.KycStatus;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .externalId(request.getExternalId())
                .kycStatus(KycStatus.PENDING)
                .active(true)
                .build();
    }

    public CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .externalId(customer.getExternalId())
                .kycStatus(customer.getKycStatus())
                .active(customer.getActive())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public CustomerCreatedEvent toEvent(Customer customer) {
        return CustomerCreatedEvent.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .externalId(customer.getExternalId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}