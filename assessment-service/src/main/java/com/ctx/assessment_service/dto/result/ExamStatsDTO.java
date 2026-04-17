package com.ctx.assessment_service.dto.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamStatsDTO {
    private double averagePercentageScore;
    private long totalExamsTaken;
    private double highestPercentageScore;
}
