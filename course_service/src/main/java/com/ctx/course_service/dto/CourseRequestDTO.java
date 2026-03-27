package com.ctx.course_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CourseRequestDTO (
    @NotBlank String title,
    @NotBlank String description,
    @NotBlank String courseCode
)
{}
