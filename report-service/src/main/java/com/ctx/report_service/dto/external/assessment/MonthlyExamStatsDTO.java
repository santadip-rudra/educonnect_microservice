package com.ctx.report_service.dto.external.assessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyExamStatsDTO {
    private int year;
    private int month;
    private double avgScore;
    private long totalExams;
    private double highestScore;
}
