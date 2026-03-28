package com.ctx.student_registry_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StudentRegistryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentRegistryServiceApplication.class, args);
	}

}
