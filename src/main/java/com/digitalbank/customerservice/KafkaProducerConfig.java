package com.digitalbank.customerservice;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.digitalbank.customerservice.kafka.CustomerCreatedEvent;

import java.util.*;

@Configuration
public class KafkaProducerConfig {
	
	 @Value("${spring.kafka.bootstrap-servers}")
	    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, CustomerCreatedEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, CustomerCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}