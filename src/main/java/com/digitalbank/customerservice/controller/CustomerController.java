package com.digitalbank.customerservice.controller;
import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digitalbank.customerservice.dto.CustomerRequest;
import com.digitalbank.customerservice.dto.CustomerResponse;
import com.digitalbank.customerservice.dto.UpdateCustomerRequest;
import com.digitalbank.customerservice.dto.UpdateKycStatusRequest;
import com.digitalbank.customerservice.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping("/customers")
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
    	 CustomerResponse body = service.create(request);
    	  URI loc = URI.create("/api/v1/customers/" + body.getExternalId());
    	  // Heuristic: if created just now, 201; on replay (entity existed), we can return 200.
    	  // A simple way: service can set a flag; or re-check repository. Keeping 201 is also acceptable.
    	  return ResponseEntity.created(loc).body(body);
    }

    
    @GetMapping("/customers/{externalId}")
    @PreAuthorize("hasAuthority('SCOPE_fdx:customers.read')")
    public ResponseEntity<CustomerResponse> getCustomerByExternalId(@PathVariable String externalId) {
    	 CustomerResponse dto = service.getByExternalId(externalId);
    	    return ResponseEntity.ok()
    	        .eTag("\"" + dto.getVersion() + "\"")  // RFC: ETag is a quoted string
    	        .body(dto); 
    	    
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
    
    
    @PatchMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable String id, @RequestHeader(name = "If-Match", required = false) String ifMatch,
            @RequestBody UpdateCustomerRequest request) {
        Integer expected = parseIfMatch(ifMatch); // see helper below

    	
        CustomerResponse updated = service.updateCustomer(id, request, expected);
        return ResponseEntity.ok()
            .eTag("\"" + updated.getVersion() + "\"")
            .body(updated);
        
        
    }

    
    @PatchMapping("/customers/{id}/kyc-status")
    public ResponseEntity<Void> updateKycStatus(
            @PathVariable String id,
            @RequestHeader(name = "If-Match", required = false) String ifMatch,
            @RequestBody UpdateKycStatusRequest request) {
    	    Integer expected = parseIfMatch(ifMatch);
    	    Integer newVersion = service.updateKycStatus(id, request.getKycStatus(), expected);
    	    return ResponseEntity.noContent()
    	        .eTag("\"" + newVersion + "\"")
    	        .build();
    }

    
    

    private Integer parseIfMatch(String ifMatch) {
      if (ifMatch == null || ifMatch.isBlank()) return null;
      // Accept bare numbers (e.g. 3) or quoted ("3")
      String v = ifMatch.replace("\"", "").trim();
      return Integer.valueOf(v);
    }
    
}