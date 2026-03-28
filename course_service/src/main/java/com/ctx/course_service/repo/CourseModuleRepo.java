package com.ctx.course_service.repo;

import com.ctx.course_service.model.CourseModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseModuleRepo extends JpaRepository<CourseModule, UUID> {
}
