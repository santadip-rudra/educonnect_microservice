package com.ctx.report_service.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse {
    UUID userId;
    String fullName;
    String email;
    String enrollmentNumber;
    Boolean active;
}
