package com.digitalbank.customerservice.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String externalId;
}