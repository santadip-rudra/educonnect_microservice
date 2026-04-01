package com.ctx.assessment_service.client;

import com.ctx.assessment_service.dto.external_response.StudentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-management-service")
public interface UserManagementServiceClient {


    @GetMapping("/student/{studentId}")
    StudentResponse findByStudentId(@PathVariable("studentId") UUID studentId);
}
