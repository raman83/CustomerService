package com.digitalbank.customerservice.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerEventProducer {

    private final KafkaTemplate<String, CustomerCreatedEvent> kafkaTemplate;

    public void publishCustomerCreatedEvent(CustomerCreatedEvent event) {
        kafkaTemplate.send("customer-events", event.getId().toString(), event);
    }
}