package com.digitalbank.customerservice.mapper;

import com.digitalbank.customerservice.dto.*;
import com.digitalbank.customerservice.kafka.CustomerCreatedEvent;
import com.digitalbank.customerservice.model.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerRequest request);

    CustomerResponse toResponse(Customer customer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerFromRequest(UpdateCustomerRequest request, @MappingTarget Customer customer);

    @Mapping(target = "externalId", source = "externalId")
    CustomerCreatedEvent toEvent(Customer customer);
}
