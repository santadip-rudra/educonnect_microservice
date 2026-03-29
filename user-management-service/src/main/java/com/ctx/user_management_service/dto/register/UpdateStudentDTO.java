package com.ctx.user_management_service.dto.register;

import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
public class UpdateStudentDTO extends UpdateUserDTO {
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String enrollmentNumber;
    private String parentEmail;
    private Boolean isActive;
    private Boolean isVerified;
}