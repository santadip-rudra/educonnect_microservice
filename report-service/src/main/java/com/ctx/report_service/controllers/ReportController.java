package com.ctx.report_service.controllers;

import com.ctx.report_service.dto.auth_principal.CurrentUser;
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

}
