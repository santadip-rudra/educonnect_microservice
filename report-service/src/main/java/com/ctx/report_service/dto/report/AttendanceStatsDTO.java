package com.ctx.report_service.dto.report;


public record AttendanceStatsDTO(
        long totalPresent,
        long totalAbsent,
        double averageAttendanceRate
) {}