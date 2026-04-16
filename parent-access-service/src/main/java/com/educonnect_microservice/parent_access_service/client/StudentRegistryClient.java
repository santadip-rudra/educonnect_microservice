package com.educonnect_microservice.parent_access_service.client;

import com.educonnect_microservice.parent_access_service.dto.AttendanceRecordDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "student-registry-service")
public interface StudentRegistryClient {

    @GetMapping("/v2/api/attendance/of")
    List<AttendanceRecordDto> getAttendanceByStudent(@RequestParam UUID studentId);
}