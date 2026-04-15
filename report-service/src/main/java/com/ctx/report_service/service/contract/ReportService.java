package com.ctx.report_service.service.contract;

import com.ctx.report_service.dto.auth_principal.CurrentUser;
import com.ctx.report_service.dto.report.EnrollmentReportDTO;
import reactor.core.publisher.Mono;

public interface ReportService {
    Mono<EnrollmentReportDTO> getAllCourseData(String authHeader, CurrentUser currentUser);
}
