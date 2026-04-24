package com.ctx.assessment_service.client;


import com.ctx.assessment_service.dto.compliance.ComplianceViolationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "compliance-service")
public interface ComplianceServiceClient {

    @PostMapping("/v2/api/compliance-records")
    void raiseViolation(@RequestBody ComplianceViolationRequestDTO dto);
}