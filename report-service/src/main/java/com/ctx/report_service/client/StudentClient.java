package com.ctx.report_service.client;


import com.ctx.report_service.dto.external.StudentResponse;
import com.ctx.report_service.dto.report.AttendanceStatsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "student-registry-service")
public interface StudentClient {

    @GetMapping("/v2/api/attendance/stats")
    AttendanceStatsDTO getAttendanceStats();
}