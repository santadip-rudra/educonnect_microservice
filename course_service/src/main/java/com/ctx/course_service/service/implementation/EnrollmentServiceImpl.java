package com.ctx.course_service.service.implementation;

import com.ctx.course_service.clientCall.AssessmentClient;
import com.ctx.course_service.clientCall.UserManagementServiceClient;
import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.dto.assessment.AssessmentResponseDTO;
import com.ctx.course_service.dto.enrollment.EnrollmentResponseDTOServe;
import com.ctx.course_service.dto.external_response.StudentResponse;
import com.ctx.course_service.enrollment.EnrollmentResponseDTO;
import com.ctx.course_service.exceptions.custom_exceptions.ResourceNotFoundException;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.model.Enrollment;
import com.ctx.course_service.repo.CourseRepo;
import com.ctx.course_service.repo.EnrollmentRepo;
import com.ctx.course_service.service.contract.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final CourseRepo courseRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final UserManagementServiceClient userManagementServiceClient;
    private final AssessmentClient assessmentClient;

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
                course.getDuration(),
                course.getCourseId()
        );
    }

    @Override
    public boolean isStudentEnrolledToTheCourse(UUID studentId, UUID courseId) {
        return enrollmentRepo.existsByStudentIdAndCourseCourseId(studentId,courseId);
    }

    @Override
    public List<EnrollmentResponseDTO> getAllEnrollmentsByStudent(UUID studentId) {
        // 1. Fetch the student's enrollments
        List<Enrollment> enrollments = enrollmentRepo.findByStudentId(studentId);

        // If no enrollments, return early to avoid unnecessary Feign calls
        if (enrollments.isEmpty()) {
            return List.of();
        }

        try {
            // 2. Fetch the student details
            StudentResponse student = userManagementServiceClient.findByStudentId(studentId);

            // Safety check: If client returns null or student is missing
            if (student == null) {
                throw new ResourceNotFoundException("Student details could not be retrieved from User Service");
            }

            // 3. Map to DTO
            return enrollments.stream()
                    .map(enrollment -> new EnrollmentResponseDTO(
                            enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : "Unknown Course",
                            student.getFullName(),
                            enrollment.getCourse() != null ? enrollment.getCourse().getDescription() : "",
                            enrollment.getCourse() != null ? enrollment.getCourse().getDuration() : 0,
                            enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null
                    ))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            // Log the error so you can see it in the course-service console!
            System.err.println("CRITICAL ERROR in getAllEnrollmentsByStudent: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rethrowing ensures it doesn't just hang
        }
    }

    @Override
    public List<CourseResponseDTO> getAllCoursesWithModulesAndEnrollments() {
        List<Course> courseList = courseRepo.findAllCoursesWithModulesAndEnrollments();

        if(courseList == null || courseList.isEmpty()){
            throw new ResourceNotFoundException("Courses not found");
        }
        List<UUID> courseIdList = courseList.stream().map(Course::getCourseId).toList();

        var allAssessmentResponseDTOList = assessmentClient.getAllAssessmentsByListOfCourseIds(courseIdList.stream().map(id->id.toString()).toList());

        List<CourseResponseDTO> courseResponseDTOList = new ArrayList<>();

        for (Course course : courseList) {

            List<ModuleResponseDTO> moduleResponseDTOList = new ArrayList<>();

            for(CourseModule courseModule : course.getModules()){
                ModuleResponseDTO moduleResponseDTO = new ModuleResponseDTO(
                        courseModule.getContentUrl(),
                        courseModule.getModuleId(),
                        courseModule.getTitle(),
                        courseModule.getDuration(),
                        courseModule.getSequenceOrder(),
                        courseModule.getContentUrl()
                );
                moduleResponseDTOList.add(moduleResponseDTO);
            }

            List<EnrollmentResponseDTOServe> enrollmentResponseDTOServeList = new ArrayList<>();

            for (Enrollment enrollment : course.getEnrollments()){
                EnrollmentResponseDTOServe enrollmentResponseDTOServe = EnrollmentResponseDTOServe.builder()
                        .enrollmentId(enrollment.getEnrollmentId())
                        .enrolledDate(enrollment.getEnrolledDate())
                        .courseId(enrollment.getCourse().getCourseId())
                        .studentId(enrollment.getStudentId())
                        .finalGrade(enrollment.getFinalGrade())
                        .isActive(enrollment.isActive())
                        .progress(enrollment.getProgress())
                        .remainingDuration(enrollment.getRemainingDuration())
                        .build();

                enrollmentResponseDTOServeList.add(enrollmentResponseDTOServe);
            }

            List<AssessmentResponseDTO> assessmentResponseDTOList
                    = allAssessmentResponseDTOList.stream()
                    .filter(dto -> dto.getCourseId().equals(course.getCourseId())).toList();


            CourseResponseDTO courseResponseDTO = new CourseResponseDTO(
                    course.getCourseId(),
                    course.getTitle(),
                    course.getDescription(),
                    course.getCourseCode(),
                    course.getDuration(),
                    course.getTeacherId(),
                    moduleResponseDTOList,
                    assessmentResponseDTOList,
                    enrollmentResponseDTOServeList
            );

            courseResponseDTOList.add(courseResponseDTO);
        }

        return courseResponseDTOList;
    }
}