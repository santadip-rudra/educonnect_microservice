package com.ctx.compliance_service.repo;

import com.ctx.compliance_service.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoteRepo extends JpaRepository<Note, UUID> {
}
