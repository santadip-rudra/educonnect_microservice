package com.ctx.assessment_service.dto.assessment.serve.assignment;


import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssignmentServeDTO extends AssessmentServeDTO {
    private UUID assignmentId;
    private String instruction;
    private Integer noOfDocumentsToBeUploaded;
    private LocalDate dueDate;
    private String submissionStatus;
    private String submissionId;
    private List<String> attachmentUris;
    private Integer attemptCount;
    private Double maxScore;
}