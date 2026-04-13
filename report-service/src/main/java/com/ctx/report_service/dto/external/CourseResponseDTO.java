package com.ctx.report_service.dto.external;
import java.util.UUID;
import java.util.List;

public record CourseResponseDTO(
        UUID courseId,
        String title,
        String description,
        String courseCode,
        Double duration,
        UUID teacherId,
        String teacherName,
        List<Object> modules,
        List<Object> enrollments,
        List<Object> reviews
) {}