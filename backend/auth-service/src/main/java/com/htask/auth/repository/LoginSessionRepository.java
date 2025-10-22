package com.htask.auth.repository;

import com.htask.auth.entity.LoginSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginSessionRepository extends JpaRepository<LoginSession, String> {
    List<LoginSession> findByUserIdAndActiveTrue(String userId);
    Optional<LoginSession> findByRefreshToken(String refreshToken);
    void deleteByUserId(String userId);
    void deleteByExpiresAtBefore(LocalDateTime date);
    Long countByUserIdAndActiveTrue(String userId);
}
