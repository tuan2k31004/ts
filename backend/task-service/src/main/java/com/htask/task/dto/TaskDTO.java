package com.htask.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private String id;
    private String title;
    private String description;
    private String projectId;
    private String creatorId;
    private String assigneeId;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private Integer position;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
