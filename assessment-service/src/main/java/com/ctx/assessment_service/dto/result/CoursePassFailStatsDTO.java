package com.ctx.assessment_service.dto.result;

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

    public double getPassPercentage() {
        return totalSubmissions == 0 ? 0.0
                : Math.round(((double) passed / totalSubmissions) * 10000.0) / 100.0;
    } // ⭐⭐Jackson will automatically include passPercentage in the JSON response because Jackson serializes all public getter methods
}
