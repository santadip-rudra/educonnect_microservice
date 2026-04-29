package com.ctx.user_management_service.dto.register;

import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTeacherDTO extends UpdateUserDTO {
    private String department;
    private String qualification;
    private String fullName;
    private Boolean isActive;
}