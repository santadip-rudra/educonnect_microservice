package com.ctx.user_management_service.dto.register;

import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class UpdateAdminDTO extends UpdateUserDTO {
    String fullName;
}
