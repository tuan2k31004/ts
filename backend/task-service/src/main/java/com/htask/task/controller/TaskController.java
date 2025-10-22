package com.htask.task.controller;

import com.htask.common.dto.ApiResponse;
import com.htask.task.dto.CreateTaskRequest;
import com.htask.task.dto.KanbanBoardDTO;
import com.htask.task.dto.TaskDTO;
import com.htask.task.dto.UpdateTaskRequest;
import com.htask.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskDTO task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created successfully", task));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(@PathVariable String taskId) {
        TaskDTO task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(ApiResponse.success(task));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks() {
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByProject(@PathVariable String projectId) {
        List<TaskDTO> tasks = taskService.getTasksByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksByAssignee(@PathVariable String assigneeId) {
        List<TaskDTO> tasks = taskService.getTasksByAssignee(assigneeId);
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/project/{projectId}/kanban")
    public ResponseEntity<ApiResponse<KanbanBoardDTO>> getKanbanBoard(@PathVariable String projectId) {
        KanbanBoardDTO kanban = taskService.getKanbanBoard(projectId);
        return ResponseEntity.ok(ApiResponse.success(kanban));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(
            @PathVariable String taskId,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskDTO task = taskService.updateTask(taskId, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", task));
    }

    @PutMapping("/{taskId}/move")
    public ResponseEntity<ApiResponse<TaskDTO>> moveTask(
            @PathVariable String taskId,
            @RequestParam String status,
            @RequestParam Integer position) {
        TaskDTO task = taskService.moveTask(taskId, status, position);
        return ResponseEntity.ok(ApiResponse.success("Task moved successfully", task));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
}
