package com.ctx.student_registry_service.dto.course;

import java.util.UUID;

public record ModuleResponseDto() {
    public record  ModuleResponseDTO (
            String contentUrl,
            UUID moduleId,
            String title,
            Double duration,
            Integer sequenceOrder
    ){
    }

}
