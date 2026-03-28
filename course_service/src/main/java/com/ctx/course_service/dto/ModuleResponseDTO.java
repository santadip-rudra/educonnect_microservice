package com.ctx.course_service.dto;

import java.util.UUID;

public record  ModuleResponseDTO (
       String contentUrl,
       UUID moduleId,
       String title,
       Double duration,
       Integer sequenceOrder

){
}
