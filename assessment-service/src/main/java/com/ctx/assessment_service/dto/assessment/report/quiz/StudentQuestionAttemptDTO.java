package com.ctx.assessment_service.dto.assessment.report.quiz;

import lombok.Data;

import java.util.UUID;

@Data
public class StudentQuestionAttemptDTO {
    private UUID questionId;
    private String questionText;

    private UUID correctOptionId;
    private String correctOptionText;

    private UUID chosenOptionId;
    private String chosenOptionText;

    private Boolean isCorrect;
}
