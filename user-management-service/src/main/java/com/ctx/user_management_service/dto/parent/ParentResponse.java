package com.ctx.user_management_service.dto.parent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParentResponse {
    private UUID parentId;
    private String phoneNumber;
    private Boolean verified;
}
