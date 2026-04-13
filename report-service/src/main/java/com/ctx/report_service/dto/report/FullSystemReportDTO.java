package com.ctx.report_service.dto.report;

import com.ctx.report_service.dto.external.*;
import com.ctx.report_service.model.Report;
import java.util.List;

public record FullSystemReportDTO(
        List<StudentResponse> students,
        List<TeacherResponseDTO> teachers,
        List<ParentResponseDTO> parents,
        List<AdminResponseDTO> admins,
        List<UserResponseDTO> allUsers,
        List<CourseResponseDTO> courses,
        List<String> roles,
        List<Report> generatedReports,
        AttendanceStatsDTO attendanceStats,
        ExamStatsDTO examStats
) {}