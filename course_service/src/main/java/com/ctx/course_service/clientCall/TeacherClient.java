package com.ctx.course_service.clientCall;


import com.ctx.course_service.dto.teacher.TeacherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name="user-management-service" , url="localhost:8081/teacher")
public interface TeacherClient {
    @GetMapping("/{id}")
    Optional<TeacherResponse> getTeacherById(@PathVariable UUID id);
}
