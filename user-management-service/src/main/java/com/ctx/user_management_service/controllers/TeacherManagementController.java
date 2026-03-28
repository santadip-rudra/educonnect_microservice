package com.ctx.user_management_service.controllers;

import com.ctx.user_management_service.dto.teacher.TeacherResponse;
import com.ctx.user_management_service.dto.teacher.TeacherUpdateRequest;
import com.ctx.user_management_service.exceptions.custom.UserNotFoundException;
import com.ctx.user_management_service.services.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("teacher")
@RequiredArgsConstructor
public class TeacherManagementController {

    private final TeacherService teacherService;

    /**
     * Updates teacher details.
     * Use PATCH if you want to strictly adhere to partial updates,
     * but PUT is commonly used in this template style.
     */
    @PutMapping
    public ResponseEntity<TeacherResponse> updateTeacher(
            @RequestHeader("X-User-Id") UUID id,
            @RequestBody TeacherUpdateRequest request) throws UserNotFoundException {
        return ResponseEntity.ok(teacherService.update(id, request));
    }

    /**
     * Retrieves a list of all teachers.
     */
    @GetMapping
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.findAllTeachers());
    }

    /**
     * Retrieves a single teacher by their ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponse> getTeacherById(@PathVariable UUID id) {
        return ResponseEntity.ok(teacherService.findByTeacherId(id));
    }
}