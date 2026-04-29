package com.ctx.assessment_service.dto.external_response;

import lombok.Data;

import java.util.UUID;

@Data
public class TeacherResponse {
    private UUID teacherId;
    private String fullName;
    private String department;
    private String qualification;
}