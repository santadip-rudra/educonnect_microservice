package com.ctx.report_service.dto.report;


import java.time.LocalDate;

public record GraphDataPointDTO(
        LocalDate date,
        Double grade
) {}