package com.ctx.assessment_service.dto.assessment.create.quiz;


import com.ctx.assessment_service.dto.assessment.create.CreateAssessmentRequestDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public class CreateQuizRequestDTO extends CreateAssessmentRequestDTO {

    private List<QuizQuestionDTO> questionDTOList;

    private Double durationMinutes;
}
