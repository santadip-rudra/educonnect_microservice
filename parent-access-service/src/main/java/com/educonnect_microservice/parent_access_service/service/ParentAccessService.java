package com.educonnect_microservice.parent_access_service.service;

import com.educonnect_microservice.parent_access_service.dto.ChildSummaryDto;

import java.util.List;
import java.util.UUID;

public interface ParentAccessService {
    void linkParent(UUID parentId, UUID studentId);
    String sendVerification(UUID parentId);
    void verifyParent(String token);
    boolean getAccess(UUID parentId, UUID studentId);
    void grantAccess(UUID parentId, UUID studentId);
    List<ChildSummaryDto> getChildren(UUID parentId);
    boolean isVerified(UUID parentId);
}
