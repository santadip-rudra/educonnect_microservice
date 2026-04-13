package com.ctx.assessment_service.dto.assessment.session.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedAnswerDTO {
    private String questionId;
    private List<String> selectedOptionIds;
}
