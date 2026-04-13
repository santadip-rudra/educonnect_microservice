package com.ctx.report_service.dto.external;
import java.util.UUID;

public record ParentResponseDTO(
        UUID userId,
        String fullName,
        String email,
        String phoneNumber,
        boolean verified
) {}