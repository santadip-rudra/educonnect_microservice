package com.ctx.student_registry_service.dto.attendance;

import java.time.LocalDate;
import java.util.List;

/**
 * One entry per day — shows all courses the student had that day
 * and their attendance status for each, plus a quick summary line.
 */
public record DailyAttendanceDTO(

        LocalDate date,

        /** e.g. "3 Present, 1 Absent" */
        String summary,

        List<CourseAttendanceDto> courses
) {
    public record CourseAttendanceDto(
            String courseName,
            String courseCode,
            String status
    ) {}
}