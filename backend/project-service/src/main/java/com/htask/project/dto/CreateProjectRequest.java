package com.htask.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateProjectRequest {
    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    @NotBlank(message = "Owner ID is required")
    private String ownerId;

    private LocalDate startDate;
    private LocalDate endDate;
}
