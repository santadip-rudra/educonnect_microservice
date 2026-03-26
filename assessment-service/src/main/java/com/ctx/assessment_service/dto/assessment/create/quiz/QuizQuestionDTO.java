package com.ctx.assessment_service.dto.assessment.create.quiz;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuizQuestionDTO {
    private UUID quizQuestionId;
    private String questionText;
    private List<QuestionOptionDTO> questionOptionDTOList;
}