package com.ctx.student_registry_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/docs")
public class DocumentController {
    public String test(){
        return "test";
    }
}
