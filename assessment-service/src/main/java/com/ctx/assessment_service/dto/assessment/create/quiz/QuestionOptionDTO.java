package com.ctx.assessment_service.dto.assessment.create.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class QuestionOptionDTO{
    private UUID questionOptionId;
    private String optionText;
    @JsonProperty("isCorrectOption")
    private Boolean isCorrectOption ;
}
