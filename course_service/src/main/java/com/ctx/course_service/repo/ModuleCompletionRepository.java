package com.ctx.course_service.repo;

import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.model.Enrollment;
import com.ctx.course_service.model.ModuleCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ModuleCompletionRepository extends JpaRepository<ModuleCompletion, UUID> {

    boolean existsByStudentIdAndModule(UUID studentId, CourseModule module);

    long countByEnrollment(Enrollment enrollment);
}
