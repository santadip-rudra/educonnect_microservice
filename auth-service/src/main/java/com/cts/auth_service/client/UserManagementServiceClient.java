package com.cts.auth_service.client;

import com.cts.auth_service.dto.AuthRegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;


@FeignClient(name = "user-management-service")
public interface UserManagementServiceClient {

    @GetMapping("/user/register")
    Map<String,String> register(@RequestBody AuthRegisterRequest authRegisterRequest);
}