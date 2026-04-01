package com.ctx.course_service.service.implementation;

import com.ctx.course_service.clientCall.UserManagementServiceClient;
import com.ctx.course_service.dto.external_response.StudentResponse;
import com.ctx.course_service.enrollment.EnrollmentResponseDTO;
import com.ctx.course_service.exceptions.custom_exceptions.ResourceNotFoundException;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.Enrollment;
import com.ctx.course_service.repo.CourseRepo;
import com.ctx.course_service.repo.EnrollmentRepo;
import com.ctx.course_service.service.contract.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final CourseRepo courseRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final UserManagementServiceClient userManagementServiceClient;


    @Override
    public EnrollmentResponseDTO enrollStudentToCourse(UUID studentId, UUID courseId) throws BadRequestException {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found!"));

        StudentResponse student = userManagementServiceClient.findByStudentId(studentId);

        if(isStudentEnrolledToTheCourse(studentId,courseId)){
            throw new BadRequestException("Student [Username: " + student.getFullName() + "] is already enrolled to the course["+course.getTitle()+"]");
        }

        Enrollment enrollment =  Enrollment.builder()
                                .course(course)
                                .isActive(true)
                                .studentId(studentId)
                                .remainingDuration(course.getDuration())
                                .progress(0.0)
                                .build();

        enrollmentRepo.save(enrollment);

        return new EnrollmentResponseDTO(
                course.getTitle(),
                student.getFullName(),
                course.getDescription(),
                course.getDuration()
        );

    }

    @Override
    public boolean isStudentEnrolledToTheCourse(UUID studentId, UUID courseId) {
        return enrollmentRepo.existsByStudentIdAndCourseCourseId(studentId,courseId);
    }
}
