package com.htask.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginSessionDTO {
    private String id;
    private String deviceType;
    private String browser;
    private String operatingSystem;
    private String ipAddress;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime lastActivity;
    private LocalDateTime expiresAt;
    private Boolean current;
}
