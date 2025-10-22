package com.htask.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Project ID is required")
    private String projectId;

    @NotBlank(message = "Creator ID is required")
    private String creatorId;

    private String assigneeId;

    private String priority = "MEDIUM";

    private LocalDate dueDate;

    private String tags;
}
