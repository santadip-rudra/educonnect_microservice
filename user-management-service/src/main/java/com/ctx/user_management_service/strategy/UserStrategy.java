package com.ctx.user_management_service.strategy;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import com.ctx.user_management_service.dto.register.AuthRegisterRequest;
import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;

import java.util.Map;
import java.util.UUID;

public interface UserStrategy {
    boolean supports(String role);
    UserResponse updateUserDetails(UpdateUserDTO updateUserDTO);
    Map<String,String> register(AuthRegisterRequest dto);
    UserResponse getUserDetails(UUID userId);
}
