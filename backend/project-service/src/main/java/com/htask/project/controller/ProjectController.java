package com.htask.project.controller;

import com.htask.common.dto.ApiResponse;
import com.htask.project.dto.*;
import com.htask.project.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectDTO>> createProject(@Valid @RequestBody CreateProjectRequest request) {
        ProjectDTO project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Project created successfully", project));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> getProjectById(@PathVariable String projectId) {
        ProjectDTO project = projectService.getProjectById(projectId);
        return ResponseEntity.ok(ApiResponse.success(project));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByOwner(@PathVariable String ownerId) {
        List<ProjectDTO> projects = projectService.getProjectsByOwner(ownerId);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ProjectDTO>>> getProjectsByUser(@PathVariable String userId) {
        List<ProjectDTO> projects = projectService.getProjectsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(projects));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDTO>> updateProject(
            @PathVariable String projectId,
            @Valid @RequestBody UpdateProjectRequest request) {
        ProjectDTO project = projectService.updateProject(projectId, request);
        return ResponseEntity.ok(ApiResponse.success("Project updated successfully", project));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity.ok(ApiResponse.success("Project deleted successfully", null));
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ApiResponse<ProjectMemberDTO>> addMember(
            @PathVariable String projectId,
            @Valid @RequestBody AddMemberRequest request) {
        ProjectMemberDTO member = projectService.addMember(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member added successfully", member));
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable String projectId,
            @PathVariable String userId) {
        projectService.removeMember(projectId, userId);
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully", null));
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity<ApiResponse<List<ProjectMemberDTO>>> getProjectMembers(@PathVariable String projectId) {
        List<ProjectMemberDTO> members = projectService.getProjectMembers(projectId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }
}
