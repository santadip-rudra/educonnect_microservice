package com.ctx.course_service.exceptions;

import com.ctx.course_service.dto.error.ErrorResponseDTO;
import com.ctx.course_service.exceptions.custom_exceptions.*;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {




    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponseDTO> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                ex.getLocalizedMessage(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }



    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<ErrorResponseDTO> handleException(Exception ex){
        ErrorResponseDTO dto = new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                ex.getLocalizedMessage(),
                ex.getMessage()
        );
        return new ResponseEntity<>(dto,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleStudentNotFoundException(UserNotFoundException ex)
    {
        ErrorResponseDTO dto=new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getLocalizedMessage(),
                ex.getMessage()
        );
        return new ResponseEntity<>(dto,HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(CourseNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleChildNotFoundException(CourseNotFoundException ex)
    {
        ErrorResponseDTO dto=new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getLocalizedMessage(),
                ex.getMessage()
        );
        return new ResponseEntity<>(dto,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ModuleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponseDTO> handleModuleNotFound(ModuleNotFoundException ex)
    {
        ErrorResponseDTO dto=new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getLocalizedMessage(),
                ex.getMessage()
        );
        return new ResponseEntity<>(dto,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<ErrorResponseDTO> handleFileIssues(FileException ex)
    {
        ErrorResponseDTO dto=new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                ex.getLocalizedMessage(),
                ex.getMessage()
        );
        return new ResponseEntity<>(dto,HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleIOException(IOException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("File storage error: " + ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public  ResponseEntity<?> handleBadRequestException(BadRequestException ex){
        return new ResponseEntity<>(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                      HttpStatus.BAD_REQUEST.value(),
                      ex.getMessage(),
                      ex.getLocalizedMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    // ResourceNotFoundException

    @ExceptionHandler(ResourceNotFoundException.class)
    public  ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex){
        return new ResponseEntity<>(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        ex.getLocalizedMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

}
