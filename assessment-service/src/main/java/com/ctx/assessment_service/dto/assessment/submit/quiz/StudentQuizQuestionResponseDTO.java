package com.ctx.assessment_service.dto.assessment.submit.quiz;

import com.ctx.assessment_service.dto.assessment.submit.AssessmentRequestDTO;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class StudentQuizQuestionResponseDTO extends AssessmentRequestDTO {
    private UUID quizId;
    List<StudentQuestionAndAnswerDTO> studentQuestionAndAnswerDTOList;
}
