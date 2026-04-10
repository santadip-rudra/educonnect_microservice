package com.ctx.user_management_service.controllers;

import com.ctx.user_management_service.dto.base_useer_response.UserResponse;
import com.ctx.user_management_service.dto.register.AuthRegisterRequest;
import com.ctx.user_management_service.dto.register.base_user.UpdateUserDTO;
import com.ctx.user_management_service.strategy.factory.RegisterAndUpdateStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final RegisterAndUpdateStrategyFactory factory;

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(@RequestBody AuthRegisterRequest dto){
        return ResponseEntity.ok(factory.getRegisterStrategy(dto.getRole()).register(dto));
    }

    @PatchMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UpdateUserDTO dto){
        return ResponseEntity.ok(factory.getRegisterStrategy(dto.getRole()).updateUserDetails(dto));
    }
}
