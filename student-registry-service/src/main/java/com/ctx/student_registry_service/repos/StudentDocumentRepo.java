package com.ctx.student_registry_service.repos;

import com.ctx.student_registry_service.models.StudentDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentDocumentRepo extends JpaRepository<StudentDocument, UUID> {
   Optional<StudentDocument> findByStudentDocumentId(UUID documentId);
}
