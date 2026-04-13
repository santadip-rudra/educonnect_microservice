package com.ctx.report_service.client;

import com.ctx.report_service.dto.external.CourseResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "course-service")
public interface CourseClient {

    @GetMapping("/course/all")
    List<CourseResponseDTO> getAllCourses();
}