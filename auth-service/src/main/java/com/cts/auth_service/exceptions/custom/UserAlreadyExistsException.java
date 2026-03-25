package com.cts.auth_service.exceptions.custom;

public class UserAlreadyExistsException extends  Exception {
    public  UserAlreadyExistsException(String message){
        super(message);
    }
}
