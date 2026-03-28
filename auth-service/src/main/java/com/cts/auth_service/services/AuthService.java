package com.cts.auth_service.services;

import com.cts.auth_service.dto.AuthRequestDto;
import com.cts.auth_service.dto.AuthResponseDto;
import com.cts.auth_service.exceptions.custom.UserNotAuthenticatedException;
import com.cts.auth_service.models.Role;
import com.cts.auth_service.models.User;
import com.cts.auth_service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthResponseDto register(AuthRequestDto requestDto){
        userRepo.save(
        User.builder()
                .username(requestDto.getUsername())
                .password(encoder.encode(requestDto.getPassword()))
                .role(Role.valueOf(requestDto.getRole()))
                .build());

        return  AuthResponseDto.builder()
                .message("registered succesfully")
                .status(true)
                .build();
    }

    public  AuthResponseDto login(AuthRequestDto requestDto) throws UsernameNotFoundException, UserNotAuthenticatedException {
        Authentication authentication =
                authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),requestDto.getPassword()
                )
               );

        if(authentication.isAuthenticated()){
            User entity = userRepo.findByUsername(requestDto.getUsername())
                    .orElseThrow(()-> new UsernameNotFoundException("User not found"));
            String jwtToken = jwtService.generateToken(entity);
            return  AuthResponseDto.builder()
                    .token(jwtToken)
                    .message("success")
                    .status(true)
                    .build();
        }
      throw  new UserNotAuthenticatedException("User is not authenticated");
    }

    public AuthResponseDto verify(String authHeader){
        if(authHeader!=null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            try{
                return jwtService.validateAndGetUserInfo(token);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
