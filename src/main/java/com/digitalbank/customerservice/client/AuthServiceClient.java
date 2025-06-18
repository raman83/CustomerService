package com.digitalbank.customerservice.client;

import com.digitalbank.customerservice.dto.CustomerRegistrationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "auth-service",
    url = "http://localhost:8081/api/v1",
    fallback = AuthServiceClientFallback.class
)
public interface AuthServiceClient {

    @PostMapping("/customer/register")
    void registerCustomer(@RequestBody CustomerRegistrationRequest request);
}