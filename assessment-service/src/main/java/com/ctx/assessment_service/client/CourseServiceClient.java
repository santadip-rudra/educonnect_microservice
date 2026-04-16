package com.ctx.assessment_service.client;

import com.ctx.assessment_service.dto.common.GenericResponse;
import com.ctx.assessment_service.dto.external_response.CourseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "course-service")
public interface CourseServiceClient {

    @GetMapping("/course/get-course/{courseId}")
    CourseResponse getcourse(@PathVariable("courseId") UUID courseId);

    @GetMapping("/enroll/student/{studentId}/course/{courseId}/check-enrollment")
    GenericResponse<Boolean> isStudentEnrolled(
            @PathVariable("studentId") UUID studentId,
            @PathVariable("courseId") UUID courseId
    );

    @PatchMapping("/enroll/internal/student/{studentId}/course/{courseId}/final-grade")
    void updateFinalGrade(
            @PathVariable("studentId") UUID studentId,
            @PathVariable("courseId") UUID courseId,
            @RequestBody Map<String, Double> body
    );
}
