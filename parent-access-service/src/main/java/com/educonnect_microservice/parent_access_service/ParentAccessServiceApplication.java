package com.educonnect_microservice.parent_access_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ParentAccessServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(ParentAccessServiceApplication.class, args);
	}

}
