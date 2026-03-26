package com.ctx.user_management_service.repo;

import com.ctx.user_management_service.models.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface ParentRepo extends JpaRepository<Parent, UUID> {
    Optional<Parent>  findByParentId(UUID parentId);
}
