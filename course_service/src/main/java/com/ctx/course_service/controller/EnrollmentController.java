package com.ctx.course_service.controller;

import com.ctx.course_service.dto.common.GenericResponse;
import com.ctx.course_service.enrollment.EnrollmentResponseDTO;
import com.ctx.course_service.service.contract.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/enroll")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("course/{courseId}/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<GenericResponse<EnrollmentResponseDTO>> enrollStudent(
            @PathVariable("studentId") UUID studentId,
            @PathVariable("courseId") UUID courseId
    ) throws BadRequestException {
        return ResponseEntity.ok(
          new GenericResponse<>(
               enrollmentService.enrollStudentToCourse(studentId,courseId),
               "Student enrolled to the course successfully",
                  HttpStatus.CREATED.value(),
                  LocalDateTime.now()
          )
        );
    }

    @GetMapping("student/{studentId}/course/{courseId}/check-enrollment")
    public ResponseEntity<GenericResponse<Boolean>> isStudentEnrolled(
            @PathVariable("studentId") UUID studentId,
            @PathVariable("courseId") UUID courseId
    ){
        boolean isEnrolled = enrollmentService.isStudentEnrolledToTheCourse(studentId,courseId);
        return ResponseEntity.ok(
                new GenericResponse<>(
                        isEnrolled,
                        isEnrolled ?
                                "Student is enrolled to the course"
                                :"Student is not enrolled to the course",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }


    @GetMapping("student/{studentId}")
    public ResponseEntity<GenericResponse<List<EnrollmentResponseDTO>>> getStudentEnrollments(
            @PathVariable("studentId") UUID studentId
    ) {
        return ResponseEntity.ok(
                new GenericResponse<>(
                        enrollmentService.getAllEnrollmentsByStudent(studentId),
                        "Student enrollments retrieved successfully",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }
}
