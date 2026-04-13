package com.ctx.report_service.client;



import com.ctx.report_service.dto.report.ExamStatsDTO;
import com.ctx.report_service.dto.report.GraphDataPointDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "assessment-service")
public interface AssessmentClient {

    @GetMapping("/assessments/stats")
    ExamStatsDTO getExamStats();

    @GetMapping("/assessments/student/{studentId}/trend")
    List<GraphDataPointDTO> getStudentTrend(@PathVariable("studentId") UUID studentId);

    @GetMapping("/assessments/course/{courseId}/trend")
    List<GraphDataPointDTO> getCourseTrend(@PathVariable("courseId") UUID courseId);

    @GetMapping("/assessments/assessment/{assessmentId}/trend") // Added this
    List<GraphDataPointDTO> getAssessmentTrend(@PathVariable("assessmentId") UUID assessmentId);
}