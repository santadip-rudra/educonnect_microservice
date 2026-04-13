package com.ctx.report_service.dto.external;
import java.util.UUID;

public record TeacherResponseDTO(
        UUID id,
        String fullName,
        String email,
        String department
) {}