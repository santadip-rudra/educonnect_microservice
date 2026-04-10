package com.ctx.compliance_service.client;

import com.ctx.compliance_service.dto.UserResponse;

import java.util.Optional;
import java.util.UUID;

public interface UserClient {
    Optional<UserResponse> findByUserId(UUID userId);
}
