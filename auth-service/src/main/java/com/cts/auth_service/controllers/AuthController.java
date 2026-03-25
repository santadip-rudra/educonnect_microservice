package com.cts.auth_service.controllers;

import com.cts.auth_service.dto.AuthRequestDto;
import com.cts.auth_service.dto.AuthResponseDto;
import com.cts.auth_service.exceptions.custom.UserAlreadyExistsException;
import com.cts.auth_service.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody AuthRequestDto request) throws UserAlreadyExistsException {
        return new ResponseEntity<>(authService.register(request),HttpStatus.CREATED);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto requestDTO) throws Exception {
        return ResponseEntity.ok(authService.login(requestDTO));
    }

    @GetMapping("validate")
    public  ResponseEntity<AuthResponseDto> validate(@RequestHeader("Authorization") String authHeader){
        AuthResponseDto response = authService.verify(authHeader);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
