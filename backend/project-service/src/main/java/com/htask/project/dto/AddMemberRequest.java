package com.htask.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMemberRequest {
    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Role is required")
    private String role; // OWNER, ADMIN, MEMBER, VIEWER
}
