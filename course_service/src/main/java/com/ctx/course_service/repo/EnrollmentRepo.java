package com.ctx.course_service.repo;

import com.ctx.course_service.model.Course;
import com.ctx.course_service.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepo extends JpaRepository<Enrollment, UUID> {

    boolean existsByStudentIdAndCourseCourseId(UUID studentId, UUID courseId);

    List<Enrollment> findByStudentId(UUID studentId);

    Optional<Enrollment> findByStudentIdAndCourse(UUID studentId, Course course);

    Optional<Enrollment> findByStudentIdAndCourseCourseId(UUID studentId, UUID courseId);

    @Query("""
            SELECT e FROM Enrollment e
            JOIN FETCH e.course c
            WHERE e.studentId = :studentId
            ORDER BY
                CASE WHEN e.finalGrade IS NULL THEN 1 ELSE 0 END ASC,
                e.finalGrade DESC,
                e.progress   DESC
            """)
    List<Enrollment> findByStudentIdOrderByScore(@Param("studentId") UUID studentId);
}