package com.ctx.assessment_service.dto.assessment.report.quiz;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class StudentQuestionAttemptDTO {
    private UUID questionId;
    private String questionText;
    private Integer marks;
    private Boolean isMultiOption;
    private Boolean isPartMarkingAllowed;

    private List<UUID>   correctOptionIds;
    private List<String> correctOptionTexts;

    private List<UUID>   chosenOptionIds;
    private List<String> chosenOptionTexts;

    private Double scoreAwarded;
}