package com.htask.project.service;

import com.htask.common.exception.BadRequestException;
import com.htask.common.exception.ResourceNotFoundException;
import com.htask.project.dto.*;
import com.htask.project.entity.Project;
import com.htask.project.entity.ProjectMember;
import com.htask.project.repository.ProjectMemberRepository;
import com.htask.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    @Transactional
    public ProjectDTO createProject(CreateProjectRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .ownerId(request.getOwnerId())
                .status(Project.ProjectStatus.PLANNING)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        project = projectRepository.save(project);

        // Add owner as project member with OWNER role
        ProjectMember ownerMember = ProjectMember.builder()
                .project(project)
                .userId(request.getOwnerId())
                .role(ProjectMember.ProjectRole.OWNER)
                .build();
        projectMemberRepository.save(ownerMember);

        return convertToDTO(project);
    }

    public ProjectDTO getProjectById(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        return convertToDTO(project);
    }

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectsByOwner(String ownerId) {
        return projectRepository.findByOwnerId(ownerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProjectDTO> getProjectsByUser(String userId) {
        List<ProjectMember> members = projectMemberRepository.findByUserId(userId);
        return members.stream()
                .map(member -> convertToDTO(member.getProject()))
                .collect(Collectors.toList());
    }

    @Transactional
    public ProjectDTO updateProject(String projectId, UpdateProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            try {
                Project.ProjectStatus status = Project.ProjectStatus.valueOf(request.getStatus().toUpperCase());
                project.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + request.getStatus());
            }
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }

        project = projectRepository.save(project);
        return convertToDTO(project);
    }

    @Transactional
    public void deleteProject(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public ProjectMemberDTO addMember(String projectId, AddMemberRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // Check if member already exists
        projectMemberRepository.findByProjectIdAndUserId(projectId, request.getUserId())
                .ifPresent(member -> {
                    throw new BadRequestException("User is already a member of this project");
                });

        try {
            ProjectMember.ProjectRole role = ProjectMember.ProjectRole.valueOf(request.getRole().toUpperCase());
            ProjectMember member = ProjectMember.builder()
                    .project(project)
                    .userId(request.getUserId())
                    .role(role)
                    .build();

            member = projectMemberRepository.save(member);
            return convertToMemberDTO(member);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + request.getRole());
        }
    }

    @Transactional
    public void removeMember(String projectId, String userId) {
        ProjectMember member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found in project"));

        if (member.getRole() == ProjectMember.ProjectRole.OWNER) {
            throw new BadRequestException("Cannot remove project owner");
        }

        projectMemberRepository.delete(member);
    }

    public List<ProjectMemberDTO> getProjectMembers(String projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }

        return projectMemberRepository.findByProjectId(projectId).stream()
                .map(this::convertToMemberDTO)
                .collect(Collectors.toList());
    }

    private ProjectDTO convertToDTO(Project project) {
        List<ProjectMemberDTO> members = projectMemberRepository.findByProjectId(project.getId()).stream()
                .map(this::convertToMemberDTO)
                .collect(Collectors.toList());

        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwnerId())
                .status(project.getStatus().name())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .members(members)
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    private ProjectMemberDTO convertToMemberDTO(ProjectMember member) {
        return ProjectMemberDTO.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .role(member.getRole().name())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
