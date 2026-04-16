package com.ctx.report_service.dto.external.assessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyAssessmentStatsDTO {
    private int year;
    private int month;
    private long totalAssessments;
    private long totalSubmissions;
}
