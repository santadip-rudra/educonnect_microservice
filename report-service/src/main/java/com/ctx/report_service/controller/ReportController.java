package com.ctx.report_service.controller;

import com.ctx.report_service.dto.report.*;
import com.ctx.report_service.dto.external.StudentResponse;
import com.ctx.report_service.service.implementation.ReportServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportServiceImpl reportService;

    @GetMapping("/summary")
    public ResponseEntity<FullSystemReportDTO> getFullReport() {
        return ResponseEntity.ok(reportService.getAllDataReport());
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentResponse>> getAllStudentsReport() {
        return ResponseEntity.ok(reportService.getAllStudents());
    }

    @GetMapping("/statistics/attendance")
    public ResponseEntity<AttendanceStatsDTO> getAttendanceStats() {
        return ResponseEntity.ok(reportService.getAttendanceStatistics());
    }

    @GetMapping("/statistics/exams")
    public ResponseEntity<ExamStatsDTO> getExamStats() {
        return ResponseEntity.ok(reportService.getExamStatistics());
    }

    @GetMapping("/performance/students/{studentId}")
    public ResponseEntity<List<GraphDataPointDTO>> getStudentTrend(@PathVariable UUID studentId) {
        return ResponseEntity.ok(reportService.getStudentPerformanceTrend(studentId));
    }

    @GetMapping("/performance/courses/{courseId}")
    public ResponseEntity<List<GraphDataPointDTO>> getCourseTrend(@PathVariable UUID courseId) {
        return ResponseEntity.ok(reportService.getCoursePerformanceTrend(courseId));
    }

    @GetMapping("/performance/assessments/{assessmentId}")
    public ResponseEntity<List<GraphDataPointDTO>> getAssessmentTrend(@PathVariable UUID assessmentId) {
        return ResponseEntity.ok(reportService.getAssessmentPerformanceTrend(assessmentId));
    }
}