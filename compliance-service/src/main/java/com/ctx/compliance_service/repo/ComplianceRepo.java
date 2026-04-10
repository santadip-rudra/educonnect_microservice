package com.ctx.compliance_service.repo;

import com.ctx.compliance_service.models.ComplianceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ComplianceRepo extends JpaRepository<ComplianceRecord, UUID> {
}
