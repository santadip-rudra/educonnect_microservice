package com.ctx.course_service.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MonthlyEnrollmentStatsDTO {
    private int year;
    private int month;
    private long totalEnrollments;
}
