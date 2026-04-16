package com.ctx.report_service.dto.external.course;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentResponseDTO {

    private UUID assessmentId;

    private Double maxScore;
    private String title;

    private String type;

    private UUID courseId;

    private UUID assignmentId;

    private UUID quizId;
}
