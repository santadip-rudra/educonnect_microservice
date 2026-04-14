package com.ctx.user_management_service.dto.auth_principal;

import lombok.Data;

import java.util.UUID;

@Data
public class CurrentUser {
    private UUID userId;
    private String Role;
    private String username;
}

