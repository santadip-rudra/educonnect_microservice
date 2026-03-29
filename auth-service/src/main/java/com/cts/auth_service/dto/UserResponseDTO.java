package com.cts.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    // Common
    private UUID userId;
    private String role;

    // Student
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String enrollmentNumber;
    private String parentEmail;
    private Boolean isActive;

    // Teacher
    private String department;
    private String qualification;

    // Parent
    private String phoneNumber;
    private Boolean verified;
}
