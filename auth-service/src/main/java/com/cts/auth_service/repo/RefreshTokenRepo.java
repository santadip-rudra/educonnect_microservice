package com.cts.auth_service.repo;

import com.cts.auth_service.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByUserUserId(UUID userId);
    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

}
