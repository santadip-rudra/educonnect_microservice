package com.educonnect_microservice.parent_access_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildSummaryDto {
    private UUID studentId;
    private String name;
    private String gradeLevel;
    private LocalDate dateOfBirth;
    private String email;
    private Double gpa;
    private Integer attendance;
    private List<EnrolledCourseDto> enrolledCourses;
}