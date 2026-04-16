package com.ctx.course_service.service.contract;

import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.enrollment.StudentCourseScoreDTO;
import com.ctx.course_service.enrollment.EnrollmentResponseDTO;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.Enrollment;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    public EnrollmentResponseDTO enrollStudentToCourse(UUID studentId, UUID courseId) throws BadRequestException;

    public boolean isStudentEnrolledToTheCourse(UUID studentId,UUID courseId);

    List<EnrollmentResponseDTO> getAllEnrollmentsByStudent(UUID studentId);

    List<CourseResponseDTO> getAllCoursesWithModulesAndEnrollments();

    void updateFinalGrade(UUID studentId, UUID courseId, Double finalGrade);

    List<StudentCourseScoreDTO> getCoursesSortedByScoreForStudent(UUID studentId);
}
