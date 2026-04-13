package com.ctx.report_service.repo;

import com.ctx.report_service.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ReportRepo extends JpaRepository<Report, UUID> {
}