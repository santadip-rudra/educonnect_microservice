package com.ctx.user_management_service.dto.admin;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class AdminResponse extends UserResponse {
    private String fullName;
}
