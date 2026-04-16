package com.ctx.report_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponse {
    UUID courseId;
    String title;
    String description;
    String courseCode;
    Double duration;
    UUID teacherId;
    String teacherName;
    List<Object> modules;
    List<Object> enrollments;
    List<Object> reviews;
}
