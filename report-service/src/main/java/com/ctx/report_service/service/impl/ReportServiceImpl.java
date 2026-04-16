package com.ctx.report_service.service.impl;

import com.ctx.report_service.dto.auth_principal.CurrentUser;
import com.ctx.report_service.dto.common.GenericResponse;
import com.ctx.report_service.dto.external.StudentResponse;
import com.ctx.report_service.dto.external.assessment.CoursePassFailStatsDTO;
import com.ctx.report_service.dto.external.assessment.MonthlyAssessmentStatsDTO;
import com.ctx.report_service.dto.external.assessment.MonthlyExamStatsDTO;
import com.ctx.report_service.dto.external.course.CourseCompletionStatsDTO;
import com.ctx.report_service.dto.external.course.CourseResponseDTO;
import com.ctx.report_service.dto.report.EnrollmentReportDTO;
import com.ctx.report_service.service.contract.ReportService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final WebClient courseServiceClient;
    private final WebClient userServiceClient;
    private final WebClient assessmentServiceClient;

    public ReportServiceImpl(
            @Qualifier("courseServiceClient")     WebClient courseServiceClient,
            @Qualifier("userServiceClient")       WebClient userServiceClient,
            @Qualifier("assessmentServiceClient") WebClient assessmentServiceClient) {
        this.courseServiceClient     = courseServiceClient;
        this.userServiceClient       = userServiceClient;
        this.assessmentServiceClient = assessmentServiceClient;
    }

    @Override
    public Mono<EnrollmentReportDTO> getAllCourseData(String authHeader, CurrentUser currentUser) {

        Mono<List<CourseResponseDTO>> courseData = courseServiceClient.get()
                .uri("/course/course-data/all")
                .header("Authorization", authHeader)
                .header("X-User-Id",       currentUser.getUserId().toString())
                .header("X-User-Role",     currentUser.getRole())
                .header("X-User-username", currentUser.getUsername())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GenericResponse<List<CourseResponseDTO>>>() {})
                .map(GenericResponse::getData);

        Mono<List<StudentResponse>> studentData = userServiceClient.get()
                .uri("/user/STUDENT/all")
                .header("Authorization", authHeader)
                .header("X-User-Id",       currentUser.getUserId().toString())
                .header("X-User-Role",     currentUser.getRole())
                .header("X-User-username", currentUser.getUsername())
                .retrieve()
                .bodyToFlux(StudentResponse.class)
                .collectList();

        return Mono.zip(courseData, studentData, EnrollmentReportDTO::new);
    }

    @Override
    public Mono<List<MonthlyExamStatsDTO>> getMonthlyExamStats(String authHeader,CurrentUser user){

        return assessmentServiceClient.get()
                .uri("/assessment/monthly-exam-stats")
                .header("Authorization",authHeader)
                .header("X-User-Id",user.getUserId().toString())
                .header("X-User-Role",     user.getRole())
                .header("X-User-username", user.getUsername())
                .retrieve()
                .bodyToFlux(MonthlyExamStatsDTO.class)
                .collectList();
    }

    @Override
    public Mono<List<MonthlyAssessmentStatsDTO>> getMonthlyAssessmentAndSubmissionStats(String authHeader,CurrentUser user){

        return assessmentServiceClient.get()
                .uri("/assessment/monthly-assessment-submission-stats")
                .header("Authorization",authHeader)
                .header("X-User-Id",user.getUserId().toString())
                .header("X-User-Role",     user.getRole())
                .header("X-User-username", user.getUsername())
                .retrieve()
                .bodyToFlux(MonthlyAssessmentStatsDTO.class)
                .collectList();
    }

    @Override
    public Mono<List<CourseCompletionStatsDTO>> getCourseCompletionStats(String authHeader, CurrentUser user){
        return courseServiceClient.get()
                .uri("/course/course-completion-data/all")
                .header("Authorization",authHeader)
                .header("X-User-Id",user.getUserId().toString())
                .header("X-User-Role",     user.getRole())
                .header("X-User-username", user.getUsername())
                .retrieve()
                .bodyToFlux(CourseCompletionStatsDTO.class)
                .collectList();

    }

    @Override
    public Mono<List<CoursePassFailStatsDTO>> getCoursePassFailStats(String authHeader, CurrentUser user) {
        return assessmentServiceClient.get()
                .uri("/assessment/pass-fail-stats/by-course")
                .header("Authorization", authHeader)
                .header("X-User-Id",       user.getUserId().toString())
                .header("X-User-Role",     user.getRole())
                .header("X-User-username", user.getUsername())
                .retrieve()
                .bodyToFlux(CoursePassFailStatsDTO.class)
                .collectList();
    }

}