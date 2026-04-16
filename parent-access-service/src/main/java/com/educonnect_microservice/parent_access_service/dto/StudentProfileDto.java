package com.educonnect_microservice.parent_access_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfileDto {
    private UUID studentId;
    private String fullName;
    private String email;
    private LocalDate dateOfBirth;
    private String enrollmentNumber;
}