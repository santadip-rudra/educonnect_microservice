package com.ctx.assessment_service.dto.assessment.serve.quiz;

import com.ctx.assessment_service.dto.assessment.serve.AssessmentServeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@ToString
@Data
public class QuizServeDTO extends AssessmentServeDTO {
    private UUID quizId;
    private Double durationInMinutes;
    List<QuizQuestionServeDTO> quizQuestionServeDTOList;
}
