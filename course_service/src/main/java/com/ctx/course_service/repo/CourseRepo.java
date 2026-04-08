package com.ctx.course_service.repo;

import com.ctx.course_service.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepo extends JpaRepository<Course, UUID> {

    @Query("SELECT c FROM Course c " +
            "JOIN FETCH c.modules " +
            "WHERE c.teacherId = :teacherId")
    List<Course> findAllCoursesByTeacherId(@Param("teacherId") UUID teacherId);
}
