package com.ctx.compliance_service.dto;

import com.ctx.compliance_service.models.ComplianceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplianceRecordRequestDTO {
    private UUID userId; // Works for both Student and Teacher IDs
    private ComplianceType type;
    private String result;
    private LocalDate date;
    private List<String> notes;
}