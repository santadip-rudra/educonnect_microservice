package com.cts.auth_service.controllers;

import com.cts.auth_service.dto.AuthResponseDto;
import com.cts.auth_service.dto.UserResponseDTO;
import com.cts.auth_service.exceptions.custom.TokenNotFoundException;
import com.cts.auth_service.models.RefreshToken;
import com.cts.auth_service.models.User;
import com.cts.auth_service.services.JwtService;
import com.cts.auth_service.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("session")
@RequiredArgsConstructor
public class SessionController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @GetMapping("refresh")
    public ResponseEntity<AuthResponseDto> refreshSession(
            @RequestHeader("X-Refresh-Token") String refreshToken) throws Exception {
        RefreshToken tokenEntity = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new TokenNotFoundException("Refresh token not found in database"));
        refreshTokenService.verifyToken(tokenEntity);
        User user = tokenEntity.getUser();
        String accessToken = jwtService.generateToken(user);

        AuthResponseDto dto = AuthResponseDto.builder()
                .refreshToken(tokenEntity.getToken())
                .accessToken(accessToken)
                .role(user.getRole().toString())
                .userId(user.getUserId())
                .build();

        return ResponseEntity.ok(dto);
    }

}
