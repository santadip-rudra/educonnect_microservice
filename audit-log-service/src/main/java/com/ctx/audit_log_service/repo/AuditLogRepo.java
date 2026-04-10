package com.ctx.audit_log_service.repo;

import com.ctx.audit_log_service.models.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepo extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByUserId(UUID userId);
    Page<AuditLog> findAllByUserId(UUID userId, Pageable pageable);
    Page<AuditLog> findAllByResource(String resource, Pageable pageable);
    Page<AuditLog> findAll(Pageable pageable);
}
