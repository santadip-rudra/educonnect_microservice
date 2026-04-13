package com.ctx.assessment_service.controller;

import com.ctx.assessment_service.dto.report.ExamStatsDTO;
import com.ctx.assessment_service.dto.report.GraphDataPointDTO;
import com.ctx.assessment_service.repo.result.ResultRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
public class AssessmentReportController {

    private final ResultRepo resultRepo;

    @GetMapping("/stats")
    public ResponseEntity<ExamStatsDTO> getExamStats() {
        return ResponseEntity.ok(resultRepo.getGlobalExamStats());
    }

    @GetMapping("/student/{studentId}/trend")
    public ResponseEntity<List<GraphDataPointDTO>> getStudentTrend(@PathVariable UUID studentId) {
        return ResponseEntity.ok(resultRepo.findTrendByStudentId(studentId));
    }

    @GetMapping("/course/{courseId}/trend")
    public ResponseEntity<List<GraphDataPointDTO>> getCourseTrend(@PathVariable UUID courseId) {
        // You'll need to add findTrendByCourseId in ResultRepo similar to the student one
        return ResponseEntity.ok(resultRepo.findTrendByCourseId(courseId));
    }

    @GetMapping("/assessment/{assessmentId}/trend")
    public ResponseEntity<List<GraphDataPointDTO>> getAssessmentTrend(@PathVariable UUID assessmentId) {
        return ResponseEntity.ok(resultRepo.findTrendByAssessmentId(assessmentId));
    }
}