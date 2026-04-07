package com.ctx.audit_log_service.controllers;

import com.ctx.audit_log_service.models.Action;
import com.ctx.audit_log_service.models.AuditLog;
import com.ctx.audit_log_service.services.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v2/api/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditLogService auditLogService;

    @PostMapping
    public ResponseEntity<AuditLog> createAudit(@RequestParam UUID userId,
                                                @RequestParam Action action,
                                                @RequestParam("resource") String resource) throws Exception{
        return  ResponseEntity.ok(auditLogService.createAudit(userId,action,resource));
    }

    /**
     * Retrieves the complete history of audit logs for a specific user.
     *
     * @param userId The unique identifier (UUID) of the user whose logs are being requested.
     * @return A {@link ResponseEntity} containing a list of {@link AuditLog} objects.
     * @throws Exception If an error occurs during the retrieval process.
     */
    @GetMapping("{userId}")
    public ResponseEntity<List<AuditLog>> viewAudits(@PathVariable UUID userId) throws Exception {
        return ResponseEntity.ok(auditLogService.findAuditLogByUserId(userId));
    }

    /**
     * Retrieves a paginated list of audit logs for a specific user.
     * The page size is currently fixed at 5 records per request.
     *
     * @param userId    The unique identifier (UUID) of the user.
     * @param startPage The page index to retrieve (0-based).
     * @return A {@link ResponseEntity} containing the requested page of {@link AuditLog} objects.
     * @throws Exception If an error occurs during the retrieval process.
     */
    @GetMapping("{userId}/from")
    public ResponseEntity<List<AuditLog>> viewAudits(
            @PathVariable UUID userId,
            @RequestParam("page") Integer startPage
    ) throws Exception {
        return ResponseEntity.ok(
                auditLogService.findAuditLogByUserId(userId, startPage, 5));
    }

    /**
     * Searches for audit logs associated with a specific resource type.
     * Results are returned in a paginated format with a fixed size of 5 records.
     *
     * @param resource  The name of the resource to filter by (e.g., "USER", "COURSE").
     * @param startPage The page index to retrieve (0-based).
     * @return A {@link ResponseEntity} containing the list of matching {@link AuditLog} records.
     * @throws Exception If an error occurs during the search process.
     */
    @GetMapping("by")
    public ResponseEntity<List<AuditLog>> viewAuditsBy(
            @RequestParam("resource") String resource,
            @RequestParam("from") Integer startPage) throws Exception {
        return ResponseEntity.ok(auditLogService.findAuditByResource(resource, startPage, 5));
    }
}
