package com.htask.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_sessions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "device_type")
    private String deviceType; // DESKTOP, MOBILE, TABLET

    @Column(name = "browser")
    private String browser;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "location")
    private String location; // City, Country

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean active = true;
}
