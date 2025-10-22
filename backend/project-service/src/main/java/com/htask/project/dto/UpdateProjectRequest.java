package com.htask.project.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProjectRequest {
    private String name;
    private String description;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
