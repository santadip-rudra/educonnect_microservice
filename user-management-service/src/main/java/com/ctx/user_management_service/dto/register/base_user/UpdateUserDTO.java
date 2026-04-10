package com.ctx.user_management_service.dto.register.base_user;

import com.ctx.user_management_service.dto.register.UpdateAdminDTO;
import com.ctx.user_management_service.dto.register.UpdateParentDTO;
import com.ctx.user_management_service.dto.register.UpdateStudentDTO;
import com.ctx.user_management_service.dto.register.UpdateTeacherDTO;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role", visible = true)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(
                        value = UpdateAdminDTO.class,
                        name = "ADMIN"
                ),
                @JsonSubTypes.Type(
                        value = UpdateTeacherDTO.class,
                        name = "TEACHER"
                ),

                @JsonSubTypes.Type(
                        value = UpdateParentDTO.class,
                        name = "PARENT"
                ),

                @JsonSubTypes.Type(
                        value = UpdateStudentDTO.class,
                        name = "STUDENT"
                )
        }
)
public class UpdateUserDTO {
    private UUID userId;
    private String role;
}
