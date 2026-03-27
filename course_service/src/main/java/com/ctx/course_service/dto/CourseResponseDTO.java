package com.ctx.course_service.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record CourseResponseDTO (
     @NotBlank   UUID courseId,
    @NotBlank
    String title,
    @NotBlank String description,
    @NotBlank  String courseCode,
    @NotBlank   Double duration,
    @NotBlank UUID teacherId,
    List<ModuleResponseDTO> moduleResponseDTO
){
}