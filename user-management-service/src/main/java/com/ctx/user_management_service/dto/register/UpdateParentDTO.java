package com.ctx.user_management_service.dto.register;

import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class UpdateParentDTO extends UpdateUserDTO {
    private String phoneNumber;
    private String fullName;
    private Boolean isActive;
    private Boolean isVerified;
}