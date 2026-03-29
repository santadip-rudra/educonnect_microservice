package com.ctx.user_management_service.strategy;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import com.ctx.user_management_service.dto.register.AuthRegisterRequest;
import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;

import java.util.Map;

public interface RegisterAndUpdateStrategy {
    boolean supports(String role);
    UserResponse updateUserDetails(UpdateUserDTO updateUserDTO);
    Map<String,String> register(AuthRegisterRequest dto);
}
