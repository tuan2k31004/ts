package com.htask.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KanbanBoardDTO {
    private String projectId;
    private Map<String, List<TaskDTO>> columns;
}
