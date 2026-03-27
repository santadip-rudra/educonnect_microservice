package com.ctx.course_service.exceptions.custom_exceptions;

public class UserIdDonotMatchException extends Exception{
    public UserIdDonotMatchException(String message)
    {
        super(message);
    }
}
