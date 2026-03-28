package com.ctx.student_registry_service.dto.demographics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmergencyContactDto {
    private String contactName;
    private String relationship;
    private String contactPhone;
}
