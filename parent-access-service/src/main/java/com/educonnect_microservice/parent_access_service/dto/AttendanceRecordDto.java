package com.educonnect_microservice.parent_access_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AttendanceRecordDto(
        UUID attendanceId,
        UUID studentId,
        UUID courseId,
        LocalDateTime date,
        String status
) {}