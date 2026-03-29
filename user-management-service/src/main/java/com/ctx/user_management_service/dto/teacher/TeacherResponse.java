package com.ctx.user_management_service.dto.teacher;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponse extends UserResponse {
    private UUID teacherId;
    private String department;
    private String qualification;
}