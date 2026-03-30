package com.ctx.assessment_service.dto.assessment.create.assignment;

import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class CreateAssignmentRequestDTO extends CreateAssessmentRequestDTO {
    private Integer noOfDocumentsToBeUploaded;
    private String instruction;
}
