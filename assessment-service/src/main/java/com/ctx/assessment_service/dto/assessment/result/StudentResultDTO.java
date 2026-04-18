package com.ctx.assessment_service.dto.assessment.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

//student quiz score

public class StudentResultDTO {
    private UUID resultId;
    private UUID studentId;
    private Double percentageScore;
    private String status;
    private UUID assessmentId;
    private String assessmentTitle;
    private Double maxScore;
    private String assessmentType;
    private UUID courseId;
    private UUID submissionId;
}