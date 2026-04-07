package com.ctx.student_registry_service.dto.attendance;

import com.ctx.student_registry_service.models.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AttendanceResponseDto(
        UUID attendanceId,
        UUID studentId,
        UUID courseId,
        LocalDateTime date,
        String status
) {

}