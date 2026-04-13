package com.ctx.report_service.dto.external;
import java.util.UUID;
import java.time.LocalDate;

public record StudentResponse(
        UUID userId,
        String fullName,
        String email,
        String enrollmentNumber,
        Boolean active
) {}