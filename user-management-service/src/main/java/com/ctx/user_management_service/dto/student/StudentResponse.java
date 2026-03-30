package com.ctx.user_management_service.dto.student;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentResponse extends UserResponse {
    private UUID studentId;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String enrollmentNumber;
    private String parentEmail;
    private Boolean isActive;
}
