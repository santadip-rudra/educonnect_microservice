package com.ctx.course_service.service.implementation;

import com.ctx.course_service.clientCall.AssessmentClient;
import com.ctx.course_service.clientCall.TeacherClient;
import com.ctx.course_service.dto.CourseRequestDTO;
import com.ctx.course_service.dto.CourseResponseDTO;
import com.ctx.course_service.dto.ModuleResponseDTO;
import com.ctx.course_service.dto.assessment.AssessmentResponseDTO;
import com.ctx.course_service.dto.enrollment.EnrollmentResponseDTOServe;
import com.ctx.course_service.enrollment.EnrollmentResponseDTO;
import com.ctx.course_service.exceptions.custom_exceptions.CourseNotFoundException;
import com.ctx.course_service.exceptions.custom_exceptions.ResourceNotFoundException;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.model.Enrollment;
import com.ctx.course_service.repo.CourseRepo;
import com.ctx.course_service.repo.EnrollmentRepo;
import com.ctx.course_service.service.contract.CourseService;
import com.ctx.course_service.service.contract.EnrollmentService;
import com.ctx.course_service.utils.mapper.CourseMapper;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for managing courses within the EduConnect platform.
 * This class handles the business logic for course creation, retrieval,
 * deletion, and student enrollment processes.
 *
 * @author sanchita das
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {


    private final CourseRepo courseRepo;

    private final CourseMapper courseMapper;

    private final TeacherClient client;

    private final AssessmentClient assessmentClient;

    @Override
    public CourseResponseDTO addCourse(CourseRequestDTO request, UUID teacherId) {
        Course course = courseMapper.toEntity(request);
        course.setTeacherId(teacherId);
        Course savedCourse = courseRepo.save(course);
        /**
         * This code sits inside the CourseServiceImpl. Once a course is successfully
         * saved to the database, we "broadcast" this event to the rest of the system.
         * we just add new Listeners without ever touching this CourseService code again."
         */
        return courseMapper.toResponseDTO(savedCourse);
    }

    /**
     * Retrieves all courses available in the system.
     *
     * @return A list of {@link CourseResponseDTO} objects.
     */
    @Override
    public List<CourseResponseDTO> getAllCourse() {
        List<Course> allCourse = courseRepo.findAll();
        return allCourse.stream().map(courseMapper::toResponseDTO).toList();
    }

    /**
     * Finds a specific course by its unique identifier.
     *
     * @param id The {@link UUID} of the course.
     * @return The corresponding {@link CourseResponseDTO}.
     * @throws CourseNotFoundException if no course exists with the given ID.
     */
    @Override
    public CourseResponseDTO getByIdCourse(UUID id) throws CourseNotFoundException {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("COURSE NOT FOUND WITH THIS ID: " + id));
        return courseMapper.toResponseDTO(course);
    }

    /**
     * Deletes a course from the database by its ID.
     *
     * @param id The {@link UUID} of the course to be removed.
     * @return A success message string.
     */
    @Override
    public String deleteById(UUID id) {
        courseRepo.deleteById(id);
        return "Deleted";
    }

    /**
     * Enrolls a student into a specific course.
     * This method initializes the enrollment progress, duration, and grades.
     *
     * @param userId   The {@link UUID} of the student.
     * @param courseId The {@link UUID} of the course to enroll in.

     * @throws CourseNotFoundException if the course ID does not exist.
     * @throws IllegalStateException   if the student is already enrolled in the course.
     */


    /**
     * Fetches all modules associated with a specific course.
     *
     * @param courseId The {@link UUID} of the target course.
     * @throws CourseNotFoundException if the course ID does not exist.
     */
    @Override
    public List<ModuleResponseDTO> getAllModulesOfACourse(UUID courseId) {
        Course c = courseRepo.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("COURSE NOT FOUND: " + courseId));
        return c.getModules().stream().map(courseMapper::getModule).toList();
    }

    @Override
    public List<CourseResponseDTO> getCoursesByTeacherId(UUID teacherId){
        List<Course> courseList = courseRepo.findAllCoursesByTeacherId(teacherId);

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
                        courseModule.getSequenceOrder()
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