package com.ctx.assessment_service.repo.assessment;


import com.ctx.assessment_service.model.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssessmentRepo extends JpaRepository<Assessment, UUID> {
    List<Assessment> findByCourseId(UUID courseId);

    @Query("SELECT a FROM Assessment a WHERE a.courseId IN ?1")
    List<Assessment> findByCoursesId(List<UUID> courseIds);
}
