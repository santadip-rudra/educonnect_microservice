package com.ctx.report_service.dto.external;

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
public class EnrollmentResponse {
    private UUID enrollmentId;

    private UUID studentId;

    private UUID courseId;

    private boolean isActive ;

    private Double remainingDuration;

    private Double progress;

    private Double finalGrade;

    private LocalDate enrolledDate;
}
