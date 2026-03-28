package com.ctx.course_service.dto;

import java.util.UUID;

public record ModuleRequestDTO (
        UUID courseId,
        String courseName
) {


}
