package com.htask.task.repository;

import com.htask.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByProjectId(String projectId);
    List<Task> findByProjectIdAndStatus(String projectId, Task.TaskStatus status);
    List<Task> findByAssigneeId(String assigneeId);
    List<Task> findByCreatorId(String creatorId);
    List<Task> findByProjectIdOrderByPositionAsc(String projectId);
}
