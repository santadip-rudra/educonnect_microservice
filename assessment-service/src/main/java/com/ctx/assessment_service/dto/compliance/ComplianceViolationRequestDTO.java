package com.ctx.assessment_service.dto.compliance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplianceViolationRequestDTO {
    private UUID userId;
    private String type;
    private String result;
    private LocalDate date;
    private List<String> notes; // ["submissionId=<uuid>", "exitCount=5", "reason=REPEATED_FULLSCREEN_EXIT"]
}