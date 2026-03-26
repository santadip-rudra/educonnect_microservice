package com.ctx.assessment_service.dto.assessment.serve;


import com.ctx.assessment_service.dto.assessment.serve.assignment.AssignmentServeDTO;
import com.ctx.assessment_service.dto.assessment.serve.quiz.QuizServeDTO;
import com.ctx.assessment_service.model.enums.AssessmentType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;


@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,property = "assessmentType", visible = true)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(
                        value = AssignmentServeDTO.class,
                        name = "ASSIGNMENT"
                ),
                @JsonSubTypes.Type(
                        value = QuizServeDTO.class,
                        name = "QUIZ"
                )
        }
)
@Data
public class AssessmentServeDTO {
    private String title;
    private AssessmentType assessmentType;
}
