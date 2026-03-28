package com.ctx.user_management_service.controllers;

import com.ctx.user_management_service.dto.student.StudentResponse;
import com.ctx.user_management_service.dto.student.StudentUpdateRequest;
import com.ctx.user_management_service.services.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("student")
@RequiredArgsConstructor
public class StudentManagementController {

    private final StudentService studentService;

    @GetMapping
    public  ResponseEntity<List<StudentResponse>> findAllStudents(){
        return  ResponseEntity.ok(studentService.findAllStudents());
    }

    @GetMapping("{studentId}")
    public  ResponseEntity<StudentResponse> findByStudentId(@PathVariable UUID studentId){
        return  ResponseEntity.ok(studentService.findByStudentId(studentId));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> updateStudent(@RequestBody StudentUpdateRequest request, @RequestHeader("X-User-Id") UUID studentId){
     return ResponseEntity.ok(studentService.update(studentId,request));
    }



}
