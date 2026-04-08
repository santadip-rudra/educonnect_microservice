package com.cts.auth_service.services;

import  com.cts.auth_service.models.RefreshToken;
import com.cts.auth_service.repo.RefreshTokenRepo;
import com.cts.auth_service.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService  {

    /** Token validity duration set to 24 hours (86,400,000 milliseconds). */
    private final Long refreshTokenDurationMS = 86400 * 1000L;

    private final SecureRandom random = new SecureRandom();
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;

    /**
     * Creates a new refresh token for a user or updates an existing one if it already exists.
     * This implementation ensures a 1-to-1 relationship between a User and a RefreshToken.
     *
     * @param userId The unique identifier of the user requesting a token.
     * @return A persisted {@link com.cts.auth_service.models.RefreshToken} with a new token string and expiry date.
     * @throws Exception if the user cannot be found in the database.
     */
    @Transactional
    public RefreshToken createToken(UUID userId) throws Exception {
        Optional<RefreshToken> optionalToken = refreshTokenRepo.findByUserUserId(userId);

        if (optionalToken.isPresent()) {
            RefreshToken token = optionalToken.get();
            token.setToken(generateToken());
            token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMS));
            refreshTokenRepo.save(token);
            return token;
        }

        return refreshTokenRepo.save(
                RefreshToken.builder()
                        .user(userRepo.findById(userId).orElseThrow(
                                () -> new Exception("User not found")
                        ))
                        .expiryDate(Instant.now().plusMillis(refreshTokenDurationMS))
                        .token(generateToken())
                        .build()
        );
    }

    /**
     * Generates a cryptographically secure random string to be used as a token.
     * Uses {@link SecureRandom} and Base64 encoding for high entropy and URL safety.
     *
     * @return A 32-byte random string, Base64 encoded without padding.
     */
    private String generateToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Validates whether a refresh token has expired.
     * If the token is expired, it is automatically removed from the database.
     *
     * @param token The {@link RefreshToken} entity to verify.
     * @return The same {@link RefreshToken} if it is still valid.
     * @throws Exception if the token has expired.
     */
    @Transactional
    public RefreshToken verifyToken(RefreshToken token) throws Exception {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.deleteByToken(token.getToken());
            throw new Exception("Invalid token : Expired");
        }
        return token;
    }

    /**
     * Retrieves a refresh token record from the database based on its string value.
     *
     * @param token An object containing the token string to search for.
     * @return An {@link Optional} containing the found RefreshToken, or empty if not found.
     * @throws Exception (Generic exception as defined in the contract).
     */
    public Optional<RefreshToken> findByToken(String token) throws Exception {
        return refreshTokenRepo.findByToken(token);
    }

    /**
     * Deletes a refresh token from the database, effectively logging out the user
     * or revoking their ability to refresh access tokens.
     *
     * @param token The string value of the token to delete.
     */
    public void deleteToken(String token) {
        refreshTokenRepo.deleteByToken(token);
    }
}