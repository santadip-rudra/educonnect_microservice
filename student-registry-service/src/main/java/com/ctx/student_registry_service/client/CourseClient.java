package com.ctx.student_registry_service.client;

import com.ctx.student_registry_service.dto.course.CourseResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "course-service", path = "/course")
public interface CourseClient {
    @GetMapping("/get-course/{courseId}")
    Optional<CourseResponseDto> findById(@PathVariable  UUID courseId);
}