package com.ctx.student_registry_service.controller;

import com.ctx.student_registry_service.dto.demographics.StudentDemographicsDTO;
import com.ctx.student_registry_service.dto.student.StudentResponse;
import com.ctx.student_registry_service.models.StudentDemographics;
import com.ctx.student_registry_service.services.StudentDemographicsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/demographics")
@RequiredArgsConstructor
public class DemographicsController {
    private final StudentDemographicsService studentDemographicsService;

    @PostMapping
    public ResponseEntity<StudentDemographics> createDemographics(
            @RequestHeader("X-User-id") UUID studentId,
            @RequestBody StudentDemographicsDTO studentDemographicsDTO) throws Exception {
        return  ResponseEntity.ok(studentDemographicsService.createDemoGraphics(studentId,studentDemographicsDTO));
    }

    @GetMapping("{studentId}")
    public ResponseEntity<StudentDemographics> findDemographicsByStudentId(@PathVariable UUID studentId) throws Exception {
      return ResponseEntity.ok(studentDemographicsService.findDemographicsById(studentId));
    }
}
