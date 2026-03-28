package com.ctx.student_registry_service.models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyContact {
    private String contactName;
    private String relationship;
    private String contactPhone;
}