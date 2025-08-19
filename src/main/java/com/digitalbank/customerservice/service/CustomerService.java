package com.digitalbank.customerservice.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.commons.exception.ConflictException;
import com.commons.exception.CustomerNotFoundException;
import com.commons.exception.PreconditionRequiredException;
import com.commons.exception.ResourceNotFoundException;
import com.commons.exception.VersionMismatchException;
import com.digitalbank.customerservice.client.AuthServiceClient;
import com.digitalbank.customerservice.dto.CustomerRegistrationRequest;
import com.digitalbank.customerservice.dto.CustomerRequest;
import com.digitalbank.customerservice.dto.CustomerResponse;
import com.digitalbank.customerservice.dto.UpdateCustomerRequest;
import com.digitalbank.customerservice.kafka.CustomerEventProducer;
import com.digitalbank.customerservice.mapper.CustomerMapper;
import com.digitalbank.customerservice.model.Customer;
import com.digitalbank.customerservice.model.KycStatus;
import com.digitalbank.customerservice.repository.CustomerRepository;
import com.digitalbank.customerservice.util.Fingerprints;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CustomerService {

    private final AuthServiceClient authServiceClient;
    private final CustomerRepository repository;
    private final CustomerMapper mapper;
    private final CustomerEventProducer eventProducer;

    public CustomerResponse create(CustomerRequest request) {
    	
    	
    	  String externalId = request.getExternalId();

    	 String fp = Fingerprints.customerCreate(
    		      request.getFirstName(), request.getLastName(),
    		      request.getEmail(), request.getPhone(), request.getAddress());

    		  // Fast path: same externalId already present?
    		  Optional<Customer> byExt = repository.findByExternalId(externalId);
    		  if (byExt.isPresent()) {
    		    Customer ex = byExt.get();
    		    if (fp.equals(ex.getRequestFingerprint())) {
    		      return mapper.toResponse(ex); // idempotent replay
    		    }
    		    throw new ConflictException("Same externalId used with different data");
    		  }

    		  // New record attempt
    		  Customer entity = mapper.toEntity(request);
    		  entity.setExternalId(externalId);
    		  entity.setActive(true);
    		  entity.setKycStatus(KycStatus.PENDING);
    		  entity.setRequestFingerprint(fp);

    		  try {
    		    Customer saved = repository.saveAndFlush(entity);
    		    eventProducer.publishCustomerCreatedEvent(mapper.toEvent(saved)); // publish once
    		    return mapper.toResponse(saved);
    		  } catch (DataIntegrityViolationException dup) {
    		    // Email/externalId unique hit → fetch winner and compare
    		    Optional<Customer> winner = repository.findByExternalId(externalId);
    		    if (winner.isPresent()) {
    		      Customer ex = winner.get();
    		      if (fp.equals(ex.getRequestFingerprint())) {
    		        return mapper.toResponse(ex); // concurrent idempotent replay
    		      }
    		      throw new ConflictException("Same externalId used with different data");
    		    }
    		    // If externalId not found, maybe email collided → treat as conflict
    		    throw new ConflictException("Duplicate email or externalId");
    		  }
    	
    	
    	
    	
       
    }

    public CustomerResponse getByExternalId(String externalId) {
        Customer customer = repository.findByExternalId(externalId)
                .orElseThrow(() ->  new ResourceNotFoundException("Customer not found with externalId: " + externalId));
;
        return mapper.toResponse(customer);
    }

    public boolean exists(String externalId) {
        return repository.findByExternalId(externalId).isPresent();
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public CustomerResponse updateCustomer(String id, UpdateCustomerRequest request,Integer expected) {
    	 Customer c = repository.findByExternalId(id)
    		        .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + id));

    		    if (expected == null) throw new PreconditionRequiredException("If-Match header required");
    		    if (!expected.equals(c.getVersion())) {
    		      throw new VersionMismatchException("Stale version. Current=" + c.getVersion() + ", If-Match=" + expected);
    		    }

    		    mapper.updateCustomerFromRequest(request, c);
    		    Customer saved = repository.save(c); // Hibernate increments version
    		    return mapper.toResponse(saved);
    }

    public Integer updateKycStatus(String id, String kycStatus,Integer expected) {
        Customer c = repository.findByExternalId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with externalId: " + id));

        
        if (expected == null) throw new PreconditionRequiredException("If-Match header required");
        if (!expected.equals(c.getVersion())) {
          throw new VersionMismatchException("Stale version. Current=" + c.getVersion() + ", If-Match=" + expected);
        }
        
        if ("VERIFIED".equalsIgnoreCase(kycStatus)) {
            CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                    c.getEmail(), "default-password", c.getExternalId()
            );
            authServiceClient.registerCustomer(request);
            c.setKycStatus(KycStatus.VERIFIED);
            c.setActive(true);
            repository.save(c);

        }
		return c.getVersion();
    }

    public Optional<CustomerResponse> getById(Long id) {
        return repository.findById(id).map(mapper::toResponse);
    }
}