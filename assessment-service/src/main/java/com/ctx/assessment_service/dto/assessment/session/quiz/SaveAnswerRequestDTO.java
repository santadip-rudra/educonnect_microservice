package com.ctx.assessment_service.dto.assessment.session.quiz;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SaveAnswerRequestDTO {
    private UUID submissionId;
    private UUID questionId;
    private List<UUID> selectedOptionIds;
}
