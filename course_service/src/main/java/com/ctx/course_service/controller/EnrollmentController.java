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
import com.ctx.course_service.dto.user.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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


    //@GetMapping("/enrollments/student/{studentId}/all")
    @GetMapping("/student/{studentId}/all")
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

    @PostMapping("/self/{courseId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<GenericResponse<EnrollmentResponseDTO>> selfEnroll(
            @PathVariable("courseId") UUID courseId,
            @AuthenticationPrincipal CurrentUser user
    ) throws BadRequestException {
        return ResponseEntity.ok(
                new GenericResponse<>(
                        enrollmentService.selfEnroll(user.getUserId(), courseId),
                        "Enrollment request submitted successfully",
                        HttpStatus.CREATED.value(),
                        LocalDateTime.now()
                )
        );
    }

    @PatchMapping("/{enrollmentId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse<EnrollmentResponseDTO>> approveEnrollment(
            @PathVariable UUID enrollmentId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                new GenericResponse<>(
                        enrollmentService.approveEnrollment(enrollmentId),
                        "Enrollment approved",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }

    @PatchMapping("/{enrollmentId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse<EnrollmentResponseDTO>> rejectEnrollment(
            @PathVariable UUID enrollmentId
    ) throws BadRequestException {
        return ResponseEntity.ok(
                new GenericResponse<>(
                        enrollmentService.rejectEnrollment(enrollmentId),
                        "Enrollment rejected",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }


}
