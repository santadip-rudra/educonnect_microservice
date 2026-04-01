package com.ctx.user_management_service.dto.base_useer_response;

import com.ctx.user_management_service.dto.admin.AdminResponse;
import com.ctx.user_management_service.dto.parent.ParentResponse;
import com.ctx.user_management_service.dto.student.StudentResponse;
import com.ctx.user_management_service.dto.teacher.TeacherResponse;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "role", visible = true)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(
                        value = AdminResponse.class,
                        name = "ADMIN"
                ),
                @JsonSubTypes.Type(
                        value = TeacherResponse.class,
                        name = "TEACHER"
                ),

                @JsonSubTypes.Type(
                        value = ParentResponse.class,
                        name = "PARENT"
                ),

                @JsonSubTypes.Type(
                        value = StudentResponse.class,
                        name = "STUDENT"
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    String role;
}
