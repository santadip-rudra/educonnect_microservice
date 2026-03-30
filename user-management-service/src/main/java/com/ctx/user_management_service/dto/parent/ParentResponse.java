package com.ctx.user_management_service.dto.parent;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ParentResponse extends UserResponse {
    private UUID parentId;
    private String phoneNumber;
    private Boolean verified;
}
