package com.ctx.assessment_service.dto.assessment.report;


import com.ctx.assessment_service.dto.assessment.report.assignment.StudentAssignmentReportDTO;
import com.ctx.assessment_service.dto.assessment.report.quiz.StudentQuizReportDTO;
import com.ctx.assessment_service.model.enums.AssessmentType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,property = "assessmentType", visible = true)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(
                        value = StudentAssignmentReportDTO.class,
                        name = "ASSIGNMENT"
                ),
                @JsonSubTypes.Type(
                        value = StudentQuizReportDTO.class,
                        name = "QUIZ"
                )
        }
)
@Data
public class AssessmentReportDTO {
    private String title;
    private AssessmentType assessmentType;
}
