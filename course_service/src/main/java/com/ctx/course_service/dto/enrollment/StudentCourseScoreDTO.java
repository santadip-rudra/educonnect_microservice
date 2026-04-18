package com.ctx.course_service.dto.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentCourseScoreDTO {

    private UUID    courseId;
    private String  courseTitle;
    private String  courseCode;
    private String  courseDescription;

    private Double  finalGrade;

    private Double  progress;

    private Double  remainingDuration;
    private boolean isActive;
    private LocalDate enrolledDate;
    private LocalDate completedDate;
}