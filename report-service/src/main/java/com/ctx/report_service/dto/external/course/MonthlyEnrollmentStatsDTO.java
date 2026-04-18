package com.ctx.report_service.dto.external.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyEnrollmentStatsDTO {
    private int year;
    private int month;
    private long totalEnrollments;
}
