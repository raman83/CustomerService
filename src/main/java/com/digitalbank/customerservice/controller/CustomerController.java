package com.digitalbank.customerservice.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.digitalbank.customerservice.dto.CustomerRequest;
import com.digitalbank.customerservice.dto.CustomerResponse;
import com.digitalbank.customerservice.dto.UpdateCustomerRequest;
import com.digitalbank.customerservice.dto.UpdateKycStatusRequest;
import com.digitalbank.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping("/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    
    @GetMapping("/customers/external-id/{externalId}")
    @PreAuthorize("hasAuthority('SCOPE_fdx:customers.read')")
    public ResponseEntity<CustomerResponse> getCustomerByExternalId(@PathVariable String externalId) {
        return ResponseEntity.ok(service.getByExternalId(externalId));
    }
    
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Customer Service is up");
    }


    @GetMapping("/exists")
    @PreAuthorize("hasAuthority('SCOPE_fdx:customers.read')")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.existsByEmail(email));
    }
    
    @GetMapping("/customers/{externalId}/exists")
    @PreAuthorize("hasAuthority('SCOPE_fdx:customers.read')")
    public boolean exists(@PathVariable String externalId) {
        return service.exists(externalId);
    }
    
    
    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(service.updateCustomer(id, request));
    }

    
    @PatchMapping("/customers/{id}/kyc-status")
    public ResponseEntity<Void> updateKycStatus(
            @PathVariable Long id,
            @RequestBody UpdateKycStatusRequest request) {
        service.updateKycStatus(id, request.getKycStatus());
        return ResponseEntity.noContent().build();
    }

    
}