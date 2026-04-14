package com.ctx.course_service.enrollment;

import lombok.AllArgsConstructor;
import lombok.Builder; // Add this
import lombok.Data;
import lombok.NoArgsConstructor; // Good practice for DTOs

@Data
@AllArgsConstructor
@NoArgsConstructor // Added for better compatibility
@Builder          // This generates the builder() method for you
public class EnrollmentResponseDTO {
    private String courseName;
    private String studentName; // Changed to lowercase 's' (Java convention)
    private String courseDescription;
    private Double durationInSec;
}