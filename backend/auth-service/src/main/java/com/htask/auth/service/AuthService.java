package com.htask.auth.service;

import com.htask.auth.dto.AuthResponse;
import com.htask.auth.dto.LoginRequest;
import com.htask.auth.dto.RegisterRequest;
import com.htask.auth.entity.User;
import com.htask.auth.repository.UserRepository;
import com.htask.common.exception.BadRequestException;
import com.htask.common.exception.UnauthorizedException;
import com.htask.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final LoginSessionService loginSessionService;

    @Transactional
    public AuthResponse register(RegisterRequest request, String ipAddress, String userAgent) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(User.Role.MEMBER) // Default role
                .active(true)
                .build();

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getId(),
                user.getRole().name()
        );

        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        // Create login session
        loginSessionService.createSession(user.getId(), refreshToken, ipAddress, userAgent);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.getActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getId(),
                user.getRole().name()
        );

        String refreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        // Create login session
        loginSessionService.createSession(user.getId(), refreshToken, ipAddress, userAgent);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken, String ipAddress, String userAgent) {
        var token = refreshTokenService.verifyRefreshToken(refreshToken);
        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!user.getActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        String newAccessToken = jwtUtil.generateToken(
                user.getUsername(),
                user.getId(),
                user.getRole().name()
        );

        // Create a new refresh token (rotation strategy)
        String newRefreshToken = refreshTokenService.createRefreshToken(user.getId()).getToken();

        // Revoke the old refresh token and session
        refreshTokenService.revokeRefreshToken(refreshToken);
        loginSessionService.revokeSessionByRefreshToken(refreshToken);

        // Create new session for the new refresh token
        loginSessionService.createSession(user.getId(), newRefreshToken, ipAddress, userAgent);

        return AuthResponse.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeRefreshToken(refreshToken);
        loginSessionService.revokeSessionByRefreshToken(refreshToken);
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token);
    }
}
