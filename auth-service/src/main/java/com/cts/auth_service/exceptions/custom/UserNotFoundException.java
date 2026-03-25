package com.cts.auth_service.exceptions.custom;

public class UserNotFoundException extends  Exception{
    public UserNotFoundException(String message){
        super(message);
    }
}
