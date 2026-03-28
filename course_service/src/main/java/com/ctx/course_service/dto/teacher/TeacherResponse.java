package com.ctx.course_service.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponse {
    private UUID teacherId;
    private String department;
    private String qualification;
    private String fullName;
}