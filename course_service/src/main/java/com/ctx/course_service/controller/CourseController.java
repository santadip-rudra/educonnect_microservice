package com.ctx.course_service.controller;

import com.ctx.course_service.clientCall.TeacherClient;
import com.ctx.course_service.dto.CourseRequestDTO;
import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.dto.teacher.TeacherResponse;
import com.ctx.course_service.exceptions.custom_exceptions.UserIdDonotMatchException;
import com.ctx.course_service.service.contract.CourseService;
import com.ctx.course_service.service.contract.CourseVideoInerface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.codec.EncoderException;

import java.io.IOException;
import java.util.UUID;
@RequiredArgsConstructor
@RestController
@RequestMapping("course")
public class CourseController {
    private final TeacherClient client;
    private final CourseService courseService;
    private  final CourseVideoInerface courseVideoInerface;


    @GetMapping
    public String test(){
        return  "working";
    }

    @PostMapping("add-course")
    public ResponseEntity<CourseResponseDTO> addCourse(@RequestBody CourseRequestDTO course , @RequestHeader("X-User-Id") UUID teacherId) throws Exception {
        System.out.println(teacherId);
      TeacherResponse t =client.getTeacherById(teacherId)
                .orElseThrow(()-> new Exception("Teacher not found"));
        return  ResponseEntity.ok(
                courseService.addCourse(course,t.getTeacherId()));

    }

    @PostMapping("/add-module")
    public ResponseEntity<ModuleResponseDTO> addVideo(
            @RequestParam MultipartFile file,
            @RequestParam String title,
            @RequestParam UUID courseId,
            @RequestHeader("X-User-Id") UUID userId) throws IOException, EncoderException {
        return ResponseEntity.ok(courseVideoInerface.uploadVideo(file,title,courseId,userId));
    }
    }
