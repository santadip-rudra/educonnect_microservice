package com.ctx.assessment_service.client;

import com.ctx.assessment_service.dto.course.CourseDTO;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.UUID;

@FeignClient(name = "course-service")
public interface CourseClient {

    CourseDTO getCourseById(UUID courseId);

}
