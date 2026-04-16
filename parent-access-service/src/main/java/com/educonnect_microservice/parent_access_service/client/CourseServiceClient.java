package com.educonnect_microservice.parent_access_service.client;

import com.educonnect_microservice.parent_access_service.dto.CourseEnrollmentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "course-service")
public interface CourseServiceClient {

    @GetMapping("/enroll/student/{studentId}")
    CourseEnrollmentResponse getStudentEnrollments(@PathVariable UUID studentId);
}