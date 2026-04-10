package com.ctx.course_service.enrollment;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class EnrollmentResponseDTO {
    private String courseName;
    private String StudentName;
    private String courseDescription;
    private Double durationInSec;
}
