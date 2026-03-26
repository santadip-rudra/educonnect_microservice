package com.ctx.assessment_service.dto.course;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseDTO {
    private UUID courseId;
    private String title;
}
