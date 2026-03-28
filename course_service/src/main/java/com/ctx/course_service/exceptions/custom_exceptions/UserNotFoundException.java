package com.ctx.course_service.exceptions.custom_exceptions;

public class UserNotFoundException extends Exception{
    public UserNotFoundException(String msg){
        super(msg);
    }
}
