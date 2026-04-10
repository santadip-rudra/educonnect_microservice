package com.ctx.course_service.clientCall;

import com.ctx.course_service.dto.assessment.AssessmentResponseDTO;
import com.ctx.course_service.dto.common.GenericResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "assessment-service" , contextId = "AssessmentClient")
public interface AssessmentClient {
    @GetMapping("/assessment/{courseId}")
    List<AssessmentResponseDTO> getAllAssessmentUsingCourses(@PathVariable("courseId") UUID courseId);

    @PostMapping("/assessment/all/by-courses")
    List<AssessmentResponseDTO> getAllAssessmentsByListOfCourseIds(@RequestBody List<String> courseIdList);
}
