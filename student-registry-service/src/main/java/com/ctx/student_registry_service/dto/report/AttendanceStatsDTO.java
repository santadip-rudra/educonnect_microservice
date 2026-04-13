package com.ctx.student_registry_service.dto.report;

public record AttendanceStatsDTO(
        Long totalPresent,
        Long totalAbsent,
        Double averageAttendanceRate
) {}