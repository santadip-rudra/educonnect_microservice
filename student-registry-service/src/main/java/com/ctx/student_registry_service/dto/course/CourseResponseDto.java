package com.ctx.student_registry_service.dto.course;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;


public record CourseResponseDto (
        @NotBlank   UUID courseId,
        @NotBlank
        String title,
        @NotBlank String description,
        @NotBlank  String courseCode,
        @NotBlank   Double duration,
        @NotBlank UUID teacherId,
        List<ModuleResponseDto> moduleResponseDTO
){
}