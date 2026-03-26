package com.ctx.assessment_service.exception.custom_exceptions;

public class UserIdDoNothMatchException extends Exception{
    public UserIdDoNothMatchException(String msg){
        super(msg);
    }
}
