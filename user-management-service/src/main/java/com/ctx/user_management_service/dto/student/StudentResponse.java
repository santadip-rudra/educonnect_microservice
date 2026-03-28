package com.ctx.user_management_service.dto.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponse {
    private UUID studentId;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String enrollmentNumber;
    private String parentEmail;
    private Boolean isActive;
}
