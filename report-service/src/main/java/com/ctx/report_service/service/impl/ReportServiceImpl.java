package com.ctx.report_service.service.impl;

import com.ctx.report_service.dto.auth_principal.CurrentUser;
import com.ctx.report_service.dto.common.GenericResponse;
import com.ctx.report_service.dto.external.ApiResponse;
import com.ctx.report_service.dto.external.StudentResponse;
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

    public ReportServiceImpl(
            @Qualifier("courseServiceClient")     WebClient courseServiceClient,
            @Qualifier("userServiceClient")       WebClient userServiceClient) {
        this.courseServiceClient     = courseServiceClient;
        this.userServiceClient       = userServiceClient;
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
}