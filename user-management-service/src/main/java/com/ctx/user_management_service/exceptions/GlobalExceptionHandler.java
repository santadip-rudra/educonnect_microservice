package com.ctx.user_management_service.exceptions;


import com.ctx.user_management_service.dto.error.ErrorResponse;
import com.ctx.user_management_service.exceptions.custom.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnknownJsonProperty(HttpMessageNotReadableException ex){
        ErrorResponse response = ErrorResponse.builder()
                .message("Invalid field ")
                .error(ex.getLocalizedMessage())
                .localDateTime(LocalDateTime.now())
                .status(false)
                .build();
        return  ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex){
        ErrorResponse response = ErrorResponse.builder()
                .message("Student Does Not Exist")
                .error(ex.getLocalizedMessage())
                .localDateTime(LocalDateTime.now())
                .status(false)
                .build();
        return  ResponseEntity.badRequest().body(response);
    }
}
