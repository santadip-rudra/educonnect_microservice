package com.ctx.assessment_service.dto.assessment.general;

import com.ctx.assessment_service.model.Assignment;
import com.ctx.assessment_service.model.Quiz;
import com.ctx.assessment_service.model.enums.AssessmentType;
import jakarta.persistence.*;
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

    private Double weight;
}
