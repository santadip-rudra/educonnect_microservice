package com.ctx.report_service.dto.external.course;

import java.util.UUID;

public record ModuleResponseDTO(
        String contentUrl,
        UUID moduleId,
        String title,
        Double duration,
        Integer sequenceOrder,
        String moduleUri

){
}