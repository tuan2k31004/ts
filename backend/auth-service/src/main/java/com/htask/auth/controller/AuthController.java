package com.htask.auth.controller;

import com.htask.auth.dto.AuthResponse;
import com.htask.auth.dto.LoginRequest;
import com.htask.auth.dto.LoginSessionDTO;
import com.htask.auth.dto.RegisterRequest;
import com.htask.auth.dto.TokenRefreshRequest;
import com.htask.auth.service.AuthService;
import com.htask.auth.service.LoginSessionService;
import com.htask.common.dto.ApiResponse;
import com.htask.common.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LoginSessionService loginSessionService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        AuthResponse response = authService.register(request, ipAddress, userAgent);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        AuthResponse response = authService.login(request, ipAddress, userAgent);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestParam String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<String>> extractUsername(@RequestParam String token) {
        String username = authService.extractUsername(token);
        return ResponseEntity.ok(ApiResponse.success(username));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request,
            HttpServletRequest httpRequest) {
        String ipAddress = getClientIpAddress(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        AuthResponse response = authService.refreshToken(request.getRefreshToken(), ipAddress, userAgent);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody TokenRefreshRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<LoginSessionDTO>>> getUserSessions(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String refreshToken) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        List<LoginSessionDTO> sessions = loginSessionService.getUserActiveSessions(userId, refreshToken);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> revokeSession(
            @PathVariable String sessionId,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        loginSessionService.revokeSession(sessionId, userId);
        return ResponseEntity.ok(ApiResponse.success("Session revoked successfully", null));
    }

    @DeleteMapping("/sessions")
    public ResponseEntity<ApiResponse<Void>> revokeAllSessions(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String userId = jwtUtil.extractUserId(token);
        loginSessionService.revokeAllUserSessions(userId);
        return ResponseEntity.ok(ApiResponse.success("All sessions revoked successfully", null));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}
