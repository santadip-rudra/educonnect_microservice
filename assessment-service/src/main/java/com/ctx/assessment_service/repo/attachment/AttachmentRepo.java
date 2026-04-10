package com.ctx.assessment_service.repo.attachment;

import com.ctx.assessment_service.model.attachment.base_class.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttachmentRepo extends JpaRepository<Attachment, UUID> {
}
