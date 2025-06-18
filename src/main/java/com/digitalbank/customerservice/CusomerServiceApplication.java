package com.digitalbank.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CusomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CusomerServiceApplication.class, args);
	}

}
