package com.ctx.report_service.client;

import com.ctx.report_service.dto.external.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@FeignClient(name = "user-management-service")
public interface UserManagementClient {

    @GetMapping("/student")
    List<StudentResponse> getAllStudents();

    @GetMapping("/teacher")
    List<TeacherResponseDTO> getAllTeachers();

    @GetMapping("/parent")
    List<ParentResponseDTO> getAllParents();

//    @GetMapping("/user")
//    List<UserResponseDTO> getAllUsers();
}