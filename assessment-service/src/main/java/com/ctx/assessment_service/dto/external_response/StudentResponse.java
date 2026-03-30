package com.ctx.assessment_service.dto.external_response;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentResponse {
    private UUID studentId;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String enrollmentNumber;
    private String parentEmail;
    private Boolean isActive;

}
