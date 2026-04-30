package com.educonnect_microservice.parent_access_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Matches GenericResponse<List<EnrollmentResponseDTO>> from course-service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseEnrollmentResponse {
    private List<CourseEnrollmentDto> data;
    private String message;
    private Integer statusCode;
}