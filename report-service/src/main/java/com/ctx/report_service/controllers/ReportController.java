package com.ctx.report_service.controllers;

import com.ctx.report_service.dto.auth_principal.CurrentUser;
import com.ctx.report_service.dto.external.assessment.CoursePassFailStatsDTO;
import com.ctx.report_service.dto.external.assessment.MonthlyAssessmentStatsDTO;
import com.ctx.report_service.dto.external.assessment.MonthlyExamStatsDTO;
import com.ctx.report_service.service.contract.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("test")
    public String reports(){
    return "test";
    }

    @GetMapping("/course-student-data/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<?>> getAllCourseAndStudentData(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {

        return reportService.getAllCourseData(authHeader,currentUser)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/monthly-exam-stats/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<MonthlyExamStatsDTO>>> getMonthlyExamStats(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal CurrentUser currentUser
    ){
        return reportService.getMonthlyExamStats(authHeader,currentUser)
                .map(data -> ResponseEntity.ok(data));
    }

    @GetMapping("/monthly-assessment-submission-stats/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<MonthlyAssessmentStatsDTO>>> getMonthlyAssessmentAndSubmissionStats(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal CurrentUser currentUser
    ){
        return reportService.getMonthlyAssessmentAndSubmissionStats(authHeader,currentUser)
                .map(data-> ResponseEntity.ok(data));
    }

    @GetMapping("/course-completion-data/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<?>> getCourseCompletionStats(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal CurrentUser currentUser
    ){
        return reportService.getCourseCompletionStats(authHeader,currentUser)
                .map(data -> ResponseEntity.ok(data));
    }

    @GetMapping("/pass-fail-stats/by-course")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<List<CoursePassFailStatsDTO>>> getCoursePassFailStats(
            @RequestHeader("Authorization") String authHeader,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return reportService.getCoursePassFailStats(authHeader, currentUser)
                .map(data -> ResponseEntity.ok(data));
    }
}
