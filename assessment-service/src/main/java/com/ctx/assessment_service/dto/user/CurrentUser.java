package com.ctx.assessment_service.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class CurrentUser {
    private UUID userId;
    private String Role;
    private String username;
}

