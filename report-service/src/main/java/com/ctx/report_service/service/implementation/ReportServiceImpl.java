package com.ctx.report_service.service.implementation;

import com.ctx.report_service.client.*;
import com.ctx.report_service.dto.report.*;
import com.ctx.report_service.dto.external.*;
import com.ctx.report_service.repo.ReportRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl {

    private final StudentClient studentClient;
    private final AssessmentClient assessmentClient;
    private final UserManagementClient userClient;
    private final CourseClient courseClient;
    private final ReportRepo reportRepo;

    public FullSystemReportDTO getAllDataReport() {
        // Aggregating data from 4 different microservices
        return new FullSystemReportDTO(
                userClient.getAllStudents(),
                userClient.getAllTeachers(),
                userClient.getAllParents(),
                null,
                null,
                courseClient.getAllCourses(),
                List.of("TEACHER", "STUDENT", "PARENT"),
                reportRepo.findAll(),
                studentClient.getAttendanceStats(),
                assessmentClient.getExamStats()
        );
    }


    public List<StudentResponse> getAllStudents() {
        return userClient.getAllStudents();
    }

    public AttendanceStatsDTO getAttendanceStatistics() {
        return studentClient.getAttendanceStats();
    }

    public ExamStatsDTO getExamStatistics() {
        return assessmentClient.getExamStats();
    }

    public List<GraphDataPointDTO> getStudentPerformanceTrend(UUID studentId) {
        return assessmentClient.getStudentTrend(studentId);
    }

    public List<GraphDataPointDTO> getCoursePerformanceTrend(UUID courseId) {
        return assessmentClient.getCourseTrend(courseId);
    }

    public List<GraphDataPointDTO> getAssessmentPerformanceTrend(UUID assessmentId) {
        return assessmentClient.getAssessmentTrend(assessmentId);
    }
}