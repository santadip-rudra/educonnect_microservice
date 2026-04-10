package com.ctx.course_service.dto.enrollment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EnrollmentResponseDTOServe {

    private UUID enrollmentId;

    private UUID studentId;

    private UUID courseId;

    private boolean isActive ;

    private Double remainingDuration;

    private Double progress;

    private Double finalGrade;

    private LocalDate enrolledDate;
}
