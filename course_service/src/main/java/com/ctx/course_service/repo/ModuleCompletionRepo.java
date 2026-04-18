package com.ctx.course_service.repo;

import com.ctx.course_service.model.CourseModule;
import com.ctx.course_service.model.Enrollment;
import com.ctx.course_service.model.ModuleCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuleCompletionRepo extends JpaRepository<ModuleCompletion, UUID> {

    boolean existsByStudentIdAndModule(UUID studentId, CourseModule module);

    long countByEnrollment(Enrollment enrollment);

    @Query("""
            SELECT mc FROM ModuleCompletion mc
            JOIN FETCH mc.module m
            WHERE mc.enrollment = :enrollment
           """)
    List<ModuleCompletion> findByEnrollmentWithModule(@Param("enrollment") Enrollment enrollment);
}
