package com.ctx.report_service.dto.report;

public record ExamStatsDTO(
        double averageScore,
        long totalExamsTaken,
        double highestScore
) {}