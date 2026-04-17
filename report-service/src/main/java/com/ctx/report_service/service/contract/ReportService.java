package com.ctx.report_service.service.contract;

import com.ctx.report_service.dto.auth_principal.CurrentUser;
import com.ctx.report_service.dto.external.assessment.CoursePassFailStatsDTO;
import com.ctx.report_service.dto.external.assessment.MonthlyAssessmentStatsDTO;
import com.ctx.report_service.dto.external.assessment.MonthlyExamStatsDTO;
import com.ctx.report_service.dto.external.course.CourseCompletionStatsDTO;
import com.ctx.report_service.dto.external.course.MonthlyEnrollmentStatsDTO;
import com.ctx.report_service.dto.report.EnrollmentReportDTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReportService {
    Mono<EnrollmentReportDTO> getAllCourseData(String authHeader, CurrentUser currentUser);
    Mono<List<MonthlyExamStatsDTO>> getMonthlyExamStats(String authHeader, CurrentUser user);
    Mono<List<MonthlyAssessmentStatsDTO>> getMonthlyAssessmentAndSubmissionStats(String authHeader, CurrentUser user);
    Mono<List<CourseCompletionStatsDTO>> getCourseCompletionStats(String authHeader, CurrentUser user);
    Mono<List<CoursePassFailStatsDTO>> getCoursePassFailStats(String authHeader, CurrentUser user);
    Mono<List<MonthlyEnrollmentStatsDTO>> getMonthlyEnrollmentStats(String authHeader, CurrentUser user);
}
