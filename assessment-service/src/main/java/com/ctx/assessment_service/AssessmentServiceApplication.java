package com.ctx.assessment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableFeignClients
@EnableMethodSecurity
public class AssessmentServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AssessmentServiceApplication.class, args);
	}
}
