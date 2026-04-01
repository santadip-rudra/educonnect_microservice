package com.ctx.assessment_service.dto.assessment.submit.quiz;

import lombok.Data;

import java.util.UUID;

@Data
public class StudentQuestionAndAnswerDTO {
    private UUID questionId;
    private UUID questionOptionId;
}
