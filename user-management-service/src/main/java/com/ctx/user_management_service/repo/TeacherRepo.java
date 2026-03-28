package com.ctx.user_management_service.repo;

import com.ctx.user_management_service.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface TeacherRepo extends JpaRepository<Teacher, UUID> {
    Optional<Teacher> findByTeacherId(UUID teacherId);
}
