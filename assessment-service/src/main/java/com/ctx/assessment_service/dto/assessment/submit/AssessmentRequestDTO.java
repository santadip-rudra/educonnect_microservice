package com.ctx.assessment_service.dto.assessment.submit;


import com.ctx.assessment_service.dto.assessment.submit.assignment.AssignmentRequestDTO;
import com.ctx.assessment_service.dto.assessment.submit.quiz.StudentQuizQuestionResponseDTO;
import com.ctx.assessment_service.model.enums.AssessmentType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;


@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,property = "assessmentType", visible = true)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(
                    value = AssignmentRequestDTO.class,
                    name = "ASSIGNMENT"
                ),
                @JsonSubTypes.Type(
                        value = StudentQuizQuestionResponseDTO.class,
                        name = "QUIZ_SUBMISSION"
                )
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class AssessmentRequestDTO {

    private String description;
    private LocalDate dueDate;
    private UUID assessmentId;
    private AssessmentType assessmentType;
}
