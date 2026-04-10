package com.ctx.assessment_service.client;

import com.ctx.assessment_service.dto.common.GenericResponse;
import com.ctx.assessment_service.dto.external_response.CourseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "course-service")
public interface CourseServiceClient {

    @GetMapping("/course/get-course/{courseId}")
    public CourseResponse getcourse( @PathVariable("courseId") UUID courseId );


    @GetMapping("/enroll/student/{studentId}/course/{courseId}/check-enrollment")
    public GenericResponse<Boolean> isStudentEnrolled(
            @PathVariable("studentId") UUID studentId,
            @PathVariable("courseId") UUID courseId
    );
}
