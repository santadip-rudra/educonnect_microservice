package com.ctx.course_service.service.implementation;

import com.ctx.course_service.exceptions.custom_exceptions.ResourceNotFoundException;
import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.model.Enrollment;
import com.ctx.course_service.model.ModuleCompletion;
import com.ctx.course_service.repo.CourseModuleRepo;
import com.ctx.course_service.repo.EnrollmentRepo;
import com.ctx.course_service.repo.ModuleCompletionRepository;
import com.ctx.course_service.service.contract.ModuleCompletionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ModuleCompletionServiceImpl implements ModuleCompletionService {

    private final ModuleCompletionRepository moduleCompletionRepo;
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
}
