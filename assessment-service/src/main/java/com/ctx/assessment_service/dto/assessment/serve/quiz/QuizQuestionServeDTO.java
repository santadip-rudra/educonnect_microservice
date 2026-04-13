package com.ctx.assessment_service.dto.assessment.serve.quiz;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuizQuestionServeDTO {
    private UUID quizQuestionId;
    private String questionText;
    private String imageUri;
    private List<QuestionOptionServeDTO> questionOptionServeDTOList;
}
