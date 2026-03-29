package com.ctx.assessment_service.dto.external_response;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseResponse {

    private UUID courseId;
    private String title;
    private String description;
    private String courseCode;
    private Double duration;
    private UUID teacherId;

}
