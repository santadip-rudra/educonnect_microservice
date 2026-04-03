package com.ctx.student_registry_service.exceptions;

import com.ctx.student_registry_service.dto.error.ErrorResponse;
import com.ctx.student_registry_service.exceptions.custom.StudentNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleStudentNotFound(StudentNotFoundException ex){
        return new ResponseEntity<>(ErrorResponse.builder()
                .status((long) HttpStatus.NOT_FOUND.value())
                .error(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .message("Student  Not Found")
                .build(), HttpStatus.NOT_FOUND);
    }
}
