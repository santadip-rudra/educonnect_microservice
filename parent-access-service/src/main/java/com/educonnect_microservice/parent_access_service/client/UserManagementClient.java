package com.educonnect_microservice.parent_access_service.client;

import com.educonnect_microservice.parent_access_service.dto.StudentProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "user-management-service")
public interface UserManagementClient {

    @PostMapping("/parent")
    void updateParent(@RequestHeader("X-User-Id") UUID parentId,
                      @RequestBody Map<String, Object> request);

    @GetMapping("/student/{studentId}")
    StudentProfileDto getStudentProfile(@PathVariable UUID studentId);
}