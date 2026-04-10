package com.ctx.audit_log_service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUser {
    private UUID userId;
    private String role;
    private String username;
}
