package com.ctx.course_service.repo;

import com.ctx.course_service.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EnrollmentRepo extends JpaRepository<Enrollment, UUID> {
    public boolean existsByStudentIdAndCourseCourseId(UUID studentId, UUID courseId);
}
