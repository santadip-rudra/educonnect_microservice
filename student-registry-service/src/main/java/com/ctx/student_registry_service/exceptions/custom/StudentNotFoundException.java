package com.ctx.student_registry_service.exceptions.custom;

public class StudentNotFoundException extends Exception{
    public StudentNotFoundException(String message){
        super(message);
    }
}
