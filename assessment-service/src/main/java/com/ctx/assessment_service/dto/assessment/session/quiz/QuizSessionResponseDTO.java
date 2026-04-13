package com.ctx.assessment_service.dto.assessment.session.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizSessionResponseDTO {

    private UUID submissionId;
    private String startedAt;          // ISO-8601 string — matches frontend expectation
    private Double durationMinutes;
    private Boolean isResumed;
    private List<SavedAnswerDTO> savedAnswers;

}
