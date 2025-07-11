package com.digitalbank.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {
        "com.digitalbank.customerservice",
        "com.commons.security"
})
public class CusomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CusomerServiceApplication.class, args);
	}

}
