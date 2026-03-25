package com.ctx.user_management_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("students")
public class StudentManagementController {

    @GetMapping
    public  String test(@RequestHeader("X-User-Id") UUID userId, @RequestHeader("X-User-Role") String role){
        System.out.println(userId);
        System.out.println(role);
        return  "Working";
    }

}
