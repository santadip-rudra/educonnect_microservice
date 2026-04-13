package com.ctx.assessment_service.dto.report;

import java.time.LocalDate;

public record GraphDataPointDTO(
        LocalDate date,
        Double grade
) {}