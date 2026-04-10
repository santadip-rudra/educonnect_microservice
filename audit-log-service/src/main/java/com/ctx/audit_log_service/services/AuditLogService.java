package com.ctx.audit_log_service.services;

import com.ctx.audit_log_service.models.Action;
import com.ctx.audit_log_service.models.AuditLog;
import com.ctx.audit_log_service.repo.AuditLogRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuditLogService {

    private final AuditLogRepo auditLogRepo;

    public AuditLog createAudit(UUID userId, Action action, String resource) throws Exception {
        AuditLog log = AuditLog.builder().action(action)
                .resource(resource).userId(userId)
                .build();
       return  auditLogRepo.save(log);
    }

    public List<AuditLog> findAuditLogByUserId(UUID userID) throws Exception {
        return auditLogRepo.findByUserId(userID);
    }

    public List<AuditLog> findAuditLogByUserId(UUID userID, Integer from, Integer pageSize) throws Exception {
        Pageable pageable = PageRequest.of(from,pageSize);
        return auditLogRepo.findAllByUserId(userID,pageable).stream().toList();
    }

    public List<AuditLog> findAuditByResource(String resource, Integer from, Integer pageSize) throws Exception {
        return  auditLogRepo.findAllByResource(resource,PageRequest.of(from,pageSize)).stream().toList();
    }

    public  List<AuditLog> findAllAudits(@RequestParam Integer pageNumber){
        Sort sort = Sort.by("timestamp").descending();
        Pageable pageable = PageRequest.of(pageNumber,5,sort);
        return  auditLogRepo.findAll(pageable).stream().toList();
    }
}
