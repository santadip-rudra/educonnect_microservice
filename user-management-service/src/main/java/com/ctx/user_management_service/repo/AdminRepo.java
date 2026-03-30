package com.ctx.user_management_service.repo;

import com.ctx.user_management_service.models.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdminRepo extends JpaRepository<Admin, UUID> {
}
