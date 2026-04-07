package com.ctx.student_registry_service.repos;

import com.ctx.student_registry_service.models.DocType;
import com.ctx.student_registry_service.models.enums.DocTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocTypeRepo extends JpaRepository<DocType, UUID> {
    Optional<DocType> findByDocTypeName(DocTypeEnum docTypeName);
}
