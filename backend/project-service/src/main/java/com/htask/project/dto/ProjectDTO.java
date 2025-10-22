package com.htask.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<ProjectMemberDTO> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
