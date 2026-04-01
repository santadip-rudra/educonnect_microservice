package com.ctx.assessment_service.dto.assessment.report.assignment;


import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentAssignmentReportDTO extends AssessmentReportDTO {
    private Integer noOfDocumentsUploaded;
    private List<String> attachmentUriList;
}
