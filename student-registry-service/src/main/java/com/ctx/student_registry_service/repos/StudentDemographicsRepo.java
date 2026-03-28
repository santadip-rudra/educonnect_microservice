package com.ctx.student_registry_service.repos;

import com.ctx.student_registry_service.models.StudentDemographics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentDemographicsRepo extends JpaRepository<StudentDemographics, UUID> {
}
