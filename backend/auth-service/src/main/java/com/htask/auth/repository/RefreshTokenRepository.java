package com.htask.auth.repository;

import com.htask.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUserId(String userId);
    void deleteByUserId(String userId);
    void deleteByExpiryDateBefore(LocalDateTime date);
    void deleteByToken(String token);
}
