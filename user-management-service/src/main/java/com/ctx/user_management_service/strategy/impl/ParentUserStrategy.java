package com.ctx.user_management_service.strategy.impl;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import com.ctx.user_management_service.dto.parent.ParentResponse;
import com.ctx.user_management_service.dto.register.AuthRegisterRequest;
import com.ctx.user_management_service.dto.register.UpdateParentDTO;
import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;
import com.ctx.user_management_service.exceptions.custom.UserNotFoundException;
import com.ctx.user_management_service.models.Parent;
import com.ctx.user_management_service.repo.ParentRepo;
import com.ctx.user_management_service.strategy.UserStrategy;
import com.ctx.user_management_service.utils.UpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParentUserStrategy implements UserStrategy {

    private final ParentRepo parentRepo;

    @Override
    public boolean supports(String role) {
        return role.equals("PARENT");
    }

    @Override
    public UserResponse updateUserDetails(UpdateUserDTO dto) {
        Parent parent = parentRepo.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException("Parent profile does not exist"));

        UpdateParentDTO parentDTO = (UpdateParentDTO) dto;

        UpdateUtil.setIfPresent(parentDTO.getPhoneNumber(), parent::setPhoneNumber);
        UpdateUtil.setIfPresent(parentDTO.getIsActive(),parent::setIsActive);
        UpdateUtil.setIfPresent(parentDTO.getIsActive(),parent::setVerified);

        return mapToResponse(parentRepo.save(parent));
    }
    private UserResponse mapToResponse(Parent parent) {
        ParentResponse response = new ParentResponse();
        response.setParentId(parent.getParentId());
        response.setPhoneNumber(parent.getPhoneNumber());
        response.setVerified(parent.getVerified());
        return response;
    }

    @Override
    public Map<String, String> register(AuthRegisterRequest dto) {
        Parent parent = new Parent();
        parent.setFullName(dto.getUsername());
        parent.setParentId(dto.getId());
        parentRepo.save(parent);
        return Map.of("message","User[Role: " + dto.getRole() + "] registered successfully");
    }

    @Override
    public UserResponse getUserDetails(UUID userId) {
        return null;
    }
}
