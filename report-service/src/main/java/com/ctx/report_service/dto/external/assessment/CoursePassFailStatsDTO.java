package com.ctx.report_service.dto.external.assessment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoursePassFailStatsDTO {
    private UUID courseId;
    private long totalSubmissions;
    private long passed;
    private long failed;
    private double passPercentage;
}
