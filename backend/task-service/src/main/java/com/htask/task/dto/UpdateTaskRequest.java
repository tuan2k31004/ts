package com.htask.task.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {
    private String title;
    private String description;
    private String assigneeId;
    private String status;
    private String priority;
    private LocalDate dueDate;
    private Integer position;
    private String tags;
}
