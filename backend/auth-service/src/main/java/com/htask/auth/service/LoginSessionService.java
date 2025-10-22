package com.htask.auth.service;

import com.htask.auth.dto.LoginSessionDTO;
import com.htask.auth.entity.LoginSession;
import com.htask.auth.repository.LoginSessionRepository;
import com.htask.auth.util.DeviceDetector;
import com.htask.common.exception.BadRequestException;
import com.htask.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginSessionService {

    private final LoginSessionRepository sessionRepository;
    private final DeviceDetector deviceDetector;

    @Value("${session.max-concurrent:5}")
    private Integer maxConcurrentSessions;

    @Value("${jwt.refresh-expiration:604800000}") // 7 days
    private Long refreshExpiration;

    @Transactional
    public LoginSession createSession(
            String userId,
            String refreshToken,
            String ipAddress,
            String userAgent
    ) {
        // Check concurrent session limit
        Long activeSessionCount = sessionRepository.countByUserIdAndActiveTrue(userId);
        if (activeSessionCount >= maxConcurrentSessions) {
            // Revoke oldest session
            revokeOldestSession(userId);
        }

        LoginSession session = LoginSession.builder()
                .userId(userId)
                .refreshToken(refreshToken)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .deviceType(deviceDetector.detectDeviceType(userAgent))
                .browser(deviceDetector.detectBrowser(userAgent))
                .operatingSystem(deviceDetector.detectOperatingSystem(userAgent))
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .active(true)
                .build();

        return sessionRepository.save(session);
    }

    @Transactional
    public void updateSessionActivity(String refreshToken) {
        sessionRepository.findByRefreshToken(refreshToken)
                .ifPresent(session -> {
                    session.setLastActivity(LocalDateTime.now());
                    sessionRepository.save(session);
                });
    }

    public List<LoginSessionDTO> getUserActiveSessions(String userId, String currentRefreshToken) {
        return sessionRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(session -> convertToDTO(session, currentRefreshToken))
                .collect(Collectors.toList());
    }

    @Transactional
    public void revokeSession(String sessionId, String userId) {
        LoginSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!session.getUserId().equals(userId)) {
            throw new BadRequestException("Cannot revoke session of another user");
        }

        session.setActive(false);
        sessionRepository.save(session);
    }

    @Transactional
    public void revokeAllUserSessions(String userId) {
        sessionRepository.findByUserIdAndActiveTrue(userId).forEach(session -> {
            session.setActive(false);
            sessionRepository.save(session);
        });
    }

    @Transactional
    public void revokeSessionByRefreshToken(String refreshToken) {
        sessionRepository.findByRefreshToken(refreshToken)
                .ifPresent(session -> {
                    session.setActive(false);
                    sessionRepository.save(session);
                });
    }

    @Transactional
    public void revokeOldestSession(String userId) {
        sessionRepository.findByUserIdAndActiveTrue(userId).stream()
                .min((s1, s2) -> s1.getCreatedAt().compareTo(s2.getCreatedAt()))
                .ifPresent(session -> {
                    session.setActive(false);
                    sessionRepository.save(session);
                });
    }

    // Clean up expired sessions every day at 3 AM
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanExpiredSessions() {
        sessionRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private LoginSessionDTO convertToDTO(LoginSession session, String currentRefreshToken) {
        return LoginSessionDTO.builder()
                .id(session.getId())
                .deviceType(session.getDeviceType())
                .browser(session.getBrowser())
                .operatingSystem(session.getOperatingSystem())
                .ipAddress(session.getIpAddress())
                .location(session.getLocation())
                .createdAt(session.getCreatedAt())
                .lastActivity(session.getLastActivity())
                .expiresAt(session.getExpiresAt())
                .current(session.getRefreshToken().equals(currentRefreshToken))
                .build();
    }
}
