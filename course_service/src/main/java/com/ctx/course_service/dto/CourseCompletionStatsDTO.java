package com.ctx.course_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseCompletionStatsDTO {
    private String courseTitle;
    private int year;
    private int month;
    private long completedStudents;
    private double completionPercentage;
}
