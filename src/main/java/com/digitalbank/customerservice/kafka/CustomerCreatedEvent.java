package com.digitalbank.customerservice.kafka;


import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerCreatedEvent {
    private Long id;
    private String email;
    private String externalId;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
}