package com.cts.auth_service.client;

import com.cts.auth_service.dto.AuditLogDto;
import com.cts.auth_service.models.Action;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "audit-log-service", path = "/v2/api/audit")
public interface AuditLogClient {
    @PostMapping
    public ResponseEntity<AuditLogDto> createAudit(@RequestParam UUID userId,
                                                   @RequestParam Action action,
                                                   @RequestParam String resource);
}
