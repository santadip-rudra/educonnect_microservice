package com.ctx.user_management_service.repo;

import com.ctx.user_management_service.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepo extends JpaRepository<Student, UUID> {
    Optional<Student> findByStudentId(UUID uuid);
}
