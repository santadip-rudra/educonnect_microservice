package com.cts.auth_service.exceptions.custom;

public class TokenNotFoundException extends Exception {
    public TokenNotFoundException(String message){
        super(message);
    }

}
