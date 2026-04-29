package com.educonnect_microservice.parent_access_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnrolledCourseDto {
    private String courseId;
    private String title;
    private String teacherName;
}