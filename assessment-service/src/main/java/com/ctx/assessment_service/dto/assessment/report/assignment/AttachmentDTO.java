package com.ctx.assessment_service.dto.assessment.report.assignment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentDTO {
    private String uri;
    private String fileName;
}