package com.educonnect_microservice.parent_access_service.repo;

import com.educonnect_microservice.parent_access_service.entity.ParentVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParentVerificationTokenRepo extends JpaRepository<ParentVerificationToken, UUID> {

    Optional<ParentVerificationToken> findByToken(String token);

    Optional<ParentVerificationToken> findByParentId(UUID parentId);

    Optional<ParentVerificationToken> findByParentIdAndVerifiedTrue(UUID parentId);
}
