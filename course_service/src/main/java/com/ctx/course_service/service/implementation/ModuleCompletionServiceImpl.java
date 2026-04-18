package com.ctx.course_service.service.implementation;

import com.ctx.course_service.dto.enrollment.StudentCourseProgressDTO;
import com.ctx.course_service.exceptions.custom_exceptions.ResourceNotFoundException;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.model.Enrollment;
import com.ctx.course_service.model.ModuleCompletion;
import com.ctx.course_service.repo.CourseModuleRepo;
import com.ctx.course_service.repo.EnrollmentRepo;
import com.ctx.course_service.repo.ModuleCompletionRepo;
import com.ctx.course_service.service.contract.ModuleCompletionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleCompletionServiceImpl implements ModuleCompletionService {

    private final ModuleCompletionRepo moduleCompletionRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final CourseModuleRepo moduleRepo;

    @Override
    @Transactional
    public void markModuleAsComplete(UUID studentId, UUID moduleId) {

        CourseModule module = moduleRepo.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        Course course = module.getCourse();

        Enrollment enrollment = enrollmentRepo.findByStudentIdAndCourse(studentId, course)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));

        if (moduleCompletionRepo.existsByStudentIdAndModule(studentId, module)) {
            throw new IllegalStateException("Module already marked as complete");
        }

        if (enrollment.getRemainingDuration() == null) {
            enrollment.setRemainingDuration(course.getDuration());
        }

        double newRemaining = enrollment.getRemainingDuration() - module.getDuration();
        enrollment.setRemainingDuration(Math.max(newRemaining, 0.0));

        moduleCompletionRepo.save(ModuleCompletion.builder()
                .studentId(studentId)
                .module(module)
                .enrollment(enrollment)
                .build());

        long totalModules = course.getModules().size();
        long completedModules = moduleCompletionRepo.countByEnrollment(enrollment);
        double progress = (completedModules / (double) totalModules) * 100.0;
        enrollment.setProgress(progress);

        if (completedModules >= totalModules) {
            enrollment.setCompletedDate(LocalDate.now());
            enrollment.setProgress(100.0);
            enrollment.setRemainingDuration(0.0);
        }

        enrollmentRepo.save(enrollment);
    }

    @Override
    @Transactional
    public List<StudentCourseProgressDTO> getProgressPerCourse(UUID studentId) {
        List<Enrollment> enrollments = enrollmentRepo.findByStudentId(studentId);

        return enrollments.stream()
                .map(enrollment -> toProgressDTO(enrollment, studentId))
                .toList();
    }

    private StudentCourseProgressDTO toProgressDTO(Enrollment enrollment, UUID studentId) {
        Course course = enrollment.getCourse();

        List<ModuleCompletion> completions =
                moduleCompletionRepo.findByEnrollmentWithModule(enrollment);

        // using set for o(1) lookup
        Set<UUID> completedModuleIds = completions.stream()
                .map(mc -> mc.getModule().getModuleId())
                .collect(Collectors.toSet());

        Map<UUID, LocalDate> completedAtByModuleId = completions.stream()
                .collect(Collectors.toMap(
                        mc -> mc.getModule().getModuleId(),
                        ModuleCompletion::getCompletedAt
                ));

        List<StudentCourseProgressDTO.ModuleProgressDTO> moduleProgressList = course.getModules()
                .stream()
                .sorted(Comparator.comparingInt(CourseModule::getSequenceOrder))
                .map(module -> {
                    boolean completed = completedModuleIds.contains(module.getModuleId());
                    return StudentCourseProgressDTO.ModuleProgressDTO.builder()
                            .moduleId(module.getModuleId())
                            .title(module.getTitle())
                            .sequenceOrder(module.getSequenceOrder())
                            .duration(module.getDuration())
                            .completed(completed)
                            .completedAt(completed
                                    ? completedAtByModuleId.get(module.getModuleId())
                                    : null)
                            .build();
                })
                .toList();

        return StudentCourseProgressDTO.builder()
                .courseId(course.getCourseId())
                .courseTitle(course.getTitle())
                .courseCode(course.getCourseCode())
                .progress(enrollment.getProgress())
                .finalGrade(enrollment.getFinalGrade())
                .remainingDuration(enrollment.getRemainingDuration())
                .isActive(enrollment.isActive())
                .enrolledDate(enrollment.getEnrolledDate())
                .completedDate(enrollment.getCompletedDate())
                .moduleProgress(moduleProgressList)
                .build();
    }
}