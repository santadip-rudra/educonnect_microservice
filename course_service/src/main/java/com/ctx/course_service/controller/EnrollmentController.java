package com.ctx.course_service.controller;

import com.ctx.course_service.dto.common.GenericResponse;
import com.ctx.course_service.dto.enrollment.FinalGradeUpdateRequest;
import com.ctx.course_service.dto.enrollment.StudentCourseScoreDTO;
import com.ctx.course_service.enrollment.EnrollmentResponseDTO;
import com.ctx.course_service.service.contract.EnrollmentService;
import com.ctx.course_service.service.contract.ModuleCompletionService;
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
    private final ModuleCompletionService moduleCompletionService;

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

    @PatchMapping("/mark-as-complete/{moduleId}")
    public ResponseEntity<?> markModuleAsComplete(
            @RequestHeader("X-User-Id") UUID studentId,
            @RequestHeader("X-User-username") String username,
            @PathVariable("moduleId") UUID moduleId
    ) {
        moduleCompletionService.markModuleAsComplete(studentId, moduleId);
        return ResponseEntity.ok(
                new GenericResponse<>(
                        moduleId + "is marked as completed for " + username,
                        "module marked as completed successfully!",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/student/courses/by-score/{studentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('TEACHER') or hasRole('PARENT')")
    public ResponseEntity<GenericResponse<List<StudentCourseScoreDTO>>> getCoursesSortedByScore(
            @PathVariable("studentId") UUID studentId
    ) {
        List<StudentCourseScoreDTO> result = enrollmentService.getCoursesSortedByScoreForStudent(studentId);
        return ResponseEntity.ok(
                new GenericResponse<>(
                        result,
                        result.isEmpty()
                                ? "No enrollments found for this student"
                                : "Courses retrieved and sorted by score successfully",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }

    @PatchMapping("/internal/student/{studentId}/course/{courseId}/final-grade")
    public ResponseEntity<?> updateFinalGrade(
            @PathVariable UUID studentId,
            @PathVariable UUID courseId,
            @RequestBody FinalGradeUpdateRequest request
    ) {
        enrollmentService.updateFinalGrade(studentId, courseId, request.getFinalGrade());
        return ResponseEntity.ok(
                new GenericResponse<>(
                        null,
                        "updated the final grade",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }

    @GetMapping("/stats/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMonthlyEnrollmentStats() {
        return ResponseEntity.ok(
                new GenericResponse<>(
                        enrollmentService.getMonthlyEnrollmentStats(),
                        "Monthly enrollment stats retrieved successfully",
                        HttpStatus.OK.value(),
                        LocalDateTime.now()
                )
        );
    }

}