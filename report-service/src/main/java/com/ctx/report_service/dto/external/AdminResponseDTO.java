package com.ctx.report_service.dto.external;
import java.util.UUID;

public record AdminResponseDTO(
        UUID userId,
        String fullName,
        String email
) {}