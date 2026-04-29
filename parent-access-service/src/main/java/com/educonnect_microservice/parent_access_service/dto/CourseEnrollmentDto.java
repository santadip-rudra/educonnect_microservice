package com.educonnect_microservice.parent_access_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseEnrollmentDto {
    private String courseName;
    private String studentName;
    private String courseDescription;
    private Double durationInSec;
    // Extended fields for grade tracking
    private String courseId;
    private Double progress;
    private Double finalGrade;
}