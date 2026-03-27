package com.educonnect_microservice.parent_access_service.repo;

import com.educonnect_microservice.parent_access_service.entity.ParentStudentMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParentStudentRepo extends JpaRepository<ParentStudentMapping, UUID> {
    List<ParentStudentMapping> findByParentId(UUID parentId);

    Optional<ParentStudentMapping> findByParentIdAndStudentId(UUID parentId,UUID studentId);
}
