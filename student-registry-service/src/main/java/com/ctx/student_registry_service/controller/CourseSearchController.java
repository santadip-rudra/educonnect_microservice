package com.ctx.student_registry_service.controller;

import com.ctx.student_registry_service.client.CourseClient;
import com.ctx.student_registry_service.dto.course.CourseResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/student-registry")
public class CourseSearchController {

    private final CourseClient client;

    @GetMapping("/search-courses")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CourseResponseDto>> searchCourse(@RequestParam String keyword) {
        return ResponseEntity.ok(client.searchCourses(keyword));
    }
}
