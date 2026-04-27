package com.ctx.student_registry_service.dto.course;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public record CourseResponseDto(
        UUID courseId,
        String title,
        String description,
        String courseCode,
        Double duration,
        UUID teacherId,
        @JsonProperty("moduleResponseDTOList")
        List<ModuleResponseDto> modules
) {
}
