package com.ctx.student_registry_service.client;

import com.ctx.student_registry_service.dto.student.StudentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "user-management-service", path = "/student")
public interface StudentClient {
    @GetMapping("{studentId}")
    Optional<StudentResponse> findByStudentId(@PathVariable UUID studentId);

    @GetMapping("exists")
    Map<String,Object> existsBystudentId(@RequestParam UUID studentId);
}