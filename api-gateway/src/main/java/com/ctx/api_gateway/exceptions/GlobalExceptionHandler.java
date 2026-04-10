package com.ctx.api_gateway.exceptions;

import com.ctx.api_gateway.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex){
            return new ResponseEntity<>(ErrorResponse.builder()
                    .message(ex.getMessage())
                    .status((long) ex.getStatusCode().value())
                    .timestamp(LocalDateTime.now())
                    .build(), ex.getStatusCode());
    }
}
