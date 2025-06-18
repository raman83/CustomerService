package com.digitalbank.customerservice.client;

import com.digitalbank.customerservice.dto.CustomerRegistrationRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceClientFallback implements AuthServiceClient {

    @Override
    public void registerCustomer(CustomerRegistrationRequest request) {
        System.out.println("Fallback: AuthService is unavailable. Skipping customer registration.");
        // Log or queue the request for retry
    }
}