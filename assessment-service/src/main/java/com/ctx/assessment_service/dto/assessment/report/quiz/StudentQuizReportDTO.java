package com.ctx.assessment_service.dto.assessment.report.quiz;


import com.ctx.assessment_service.dto.assessment.report.AssessmentReportDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentQuizReportDTO extends AssessmentReportDTO {
    private UUID submissionId;
    List<StudentQuestionAttemptDTO> studentQuestionAttemptDTOList;
}
