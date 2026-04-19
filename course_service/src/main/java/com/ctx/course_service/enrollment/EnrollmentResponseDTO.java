package com.ctx.course_service.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentResponseDTO {
    private UUID enrollmentId;
    private String courseName;
    private String studentName;
    private String courseDescription;
    private Double durationInSec;
    private UUID courseId;
    private String enrollmentStatus;
}