package com.ctx.user_management_service.strategy.impl;

import com.ctx.user_management_service.dto.admin.AdminResponse;
import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import com.ctx.user_management_service.dto.register.AuthRegisterRequest;
import com.ctx.user_management_service.dto.register.UpdateAdminDTO;
import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;
import com.ctx.user_management_service.models.Admin;
import com.ctx.user_management_service.repo.AdminRepo;
import com.ctx.user_management_service.strategy.RegisterAndUpdateStrategy;
import com.ctx.user_management_service.utils.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminRegisterAndUpdateStrategy implements RegisterAndUpdateStrategy {
    private final AdminRepo adminRepo;

    @Override
    public boolean supports(String role) {
        return role.equals("ADMIN");
    }

    @Override
    public UserResponse updateUserDetails(UpdateUserDTO dto) {
        Admin admin = adminRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Admin profile does not exist"));

        UpdateUtil.setIfPresent(((UpdateAdminDTO)dto).getFullName(), admin::setFullName);

        return AdminResponse.builder().fullName(admin.getFullName()).build();
    }

    @Override
    public Map<String, String> register(AuthRegisterRequest dto) {
        Admin admin = new Admin();
        admin.setFullName(dto.getUsername());
        admin.setAdminId(dto.getId());
        adminRepo.save(admin);
        return Map.of("message","User[Role: " + dto.getRole() + "] registered successfully");
    }

}
