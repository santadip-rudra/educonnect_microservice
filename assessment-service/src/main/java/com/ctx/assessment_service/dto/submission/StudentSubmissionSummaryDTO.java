package com.ctx.assessment_service.dto.submission;

import com.ctx.assessment_service.model.ResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubmissionSummaryDTO {
    private UUID submissionId;
    private UUID studentId;
    private String submissionStatus;
    private Boolean isLate;
    private Integer attemptCount;
    private Integer attachmentCount;
    private LocalDateTime submittedAt;
    private Boolean graded;
    private Double percentageScore;
    private ResultStatus resultStatus;
}