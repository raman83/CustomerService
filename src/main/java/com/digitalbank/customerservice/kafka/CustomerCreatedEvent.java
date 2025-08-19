package com.digitalbank.customerservice.kafka;


import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.*;

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
    private LocalDateTime createdAt; // <-- switched from LocalDateTime
}