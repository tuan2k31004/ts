package com.htask.task.service;

import com.htask.common.exception.BadRequestException;
import com.htask.common.exception.ResourceNotFoundException;
import com.htask.task.dto.CreateTaskRequest;
import com.htask.task.dto.KanbanBoardDTO;
import com.htask.task.dto.TaskDTO;
import com.htask.task.dto.UpdateTaskRequest;
import com.htask.task.entity.Task;
import com.htask.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public TaskDTO createTask(CreateTaskRequest request) {
        Task.TaskPriority priority;
        try {
            priority = Task.TaskPriority.valueOf(request.getPriority().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid priority: " + request.getPriority());
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .projectId(request.getProjectId())
                .creatorId(request.getCreatorId())
                .assigneeId(request.getAssigneeId())
                .status(Task.TaskStatus.TODO)
                .priority(priority)
                .dueDate(request.getDueDate())
                .position(0)
                .tags(request.getTags())
                .build();

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    public TaskDTO getTaskById(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        return convertToDTO(task);
    }

    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByProject(String projectId) {
        return taskRepository.findByProjectIdOrderByPositionAsc(projectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TaskDTO> getTasksByAssignee(String assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public KanbanBoardDTO getKanbanBoard(String projectId) {
        List<Task> tasks = taskRepository.findByProjectIdOrderByPositionAsc(projectId);

        Map<String, List<TaskDTO>> columns = new LinkedHashMap<>();
        columns.put("TODO", new ArrayList<>());
        columns.put("IN_PROGRESS", new ArrayList<>());
        columns.put("IN_REVIEW", new ArrayList<>());
        columns.put("DONE", new ArrayList<>());
        columns.put("ARCHIVED", new ArrayList<>());

        tasks.forEach(task -> {
            String statusKey = task.getStatus().name();
            columns.get(statusKey).add(convertToDTO(task));
        });

        return KanbanBoardDTO.builder()
                .projectId(projectId)
                .columns(columns)
                .build();
    }

    @Transactional
    public TaskDTO updateTask(String taskId, UpdateTaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getAssigneeId() != null) {
            task.setAssigneeId(request.getAssigneeId());
        }
        if (request.getStatus() != null) {
            try {
                Task.TaskStatus status = Task.TaskStatus.valueOf(request.getStatus().toUpperCase());
                task.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + request.getStatus());
            }
        }
        if (request.getPriority() != null) {
            try {
                Task.TaskPriority priority = Task.TaskPriority.valueOf(request.getPriority().toUpperCase());
                task.setPriority(priority);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid priority: " + request.getPriority());
            }
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getPosition() != null) {
            task.setPosition(request.getPosition());
        }
        if (request.getTags() != null) {
            task.setTags(request.getTags());
        }

        task = taskRepository.save(task);
        return convertToDTO(task);
    }

    @Transactional
    public TaskDTO moveTask(String taskId, String newStatus, Integer newPosition) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        try {
            Task.TaskStatus status = Task.TaskStatus.valueOf(newStatus.toUpperCase());
            task.setStatus(status);
            task.setPosition(newPosition);
            task = taskRepository.save(task);
            return convertToDTO(task);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + newStatus);
        }
    }

    @Transactional
    public void deleteTask(String taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }

    private TaskDTO convertToDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .projectId(task.getProjectId())
                .creatorId(task.getCreatorId())
                .assigneeId(task.getAssigneeId())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .dueDate(task.getDueDate())
                .position(task.getPosition())
                .tags(task.getTags())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
