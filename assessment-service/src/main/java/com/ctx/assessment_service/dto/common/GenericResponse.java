package com.ctx.assessment_service.dto.common;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GenericResponse<T> {
    private T data;
    private String message;
    private int httpStatus;
    private LocalDateTime timestamp;
}
