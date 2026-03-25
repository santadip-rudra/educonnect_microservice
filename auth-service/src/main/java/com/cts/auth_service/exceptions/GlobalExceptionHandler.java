package com.cts.auth_service.exceptions;

import com.cts.auth_service.dto.ExceptionResponse;
import com.cts.auth_service.exceptions.custom.UserAlreadyExistsException;
import com.cts.auth_service.exceptions.custom.UserNotFoundException;
import org.apache.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleUserExists(UserAlreadyExistsException ex){
        return new ResponseEntity<>(ExceptionResponse.builder()
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.SC_BAD_REQUEST)
                .error("User already exists")
                .message(ex.getLocalizedMessage())
                .build(), HttpStatusCode.valueOf(HttpStatus.SC_BAD_REQUEST));
    }
}
