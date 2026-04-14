package com.ctx.report_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/api/reports")
@RequiredArgsConstructor
public class ReportController {
    @GetMapping("test")
    public String reports(){
    return "test";
    }
}
