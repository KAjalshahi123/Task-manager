package com.taskmanager.service.impl;

import com.taskmanager.dto.*;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.ProjectMember;
import com.taskmanager.entity.User;
import com.taskmanager.enums.Role;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.exception.BadRequestException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.repository.ProjectMemberRepository;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    @Transactional
    public ProjectDto createProject(ProjectRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdBy(user)
                .active(true)
                .build();

        Project saved = projectRepository.save(project);

        ProjectMember ownerMember = ProjectMember.builder()
                .project(saved)
                .user(user)
                .role(Role.ADMIN)
                .build();
        projectMemberRepository.save(ownerMember);

        return mapToProjectDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto getProjectById(Long id, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        if (!isMemberOrAdmin(project, userEmail)) {
            throw new UnauthorizedException("You don't have access to this project");
        }
        return mapToProjectDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getMyProjects(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return projectRepository.findByUserId(user.getId()).stream()
                .map(this::mapToProjectDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToProjectDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDto updateProject(Long id, ProjectRequest request, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!isProjectAdmin(project, userEmail)) {
            throw new UnauthorizedException("Only project admin can update");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        return mapToProjectDto(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void deleteProject(Long id, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!isProjectAdmin(project, userEmail)) {
            throw new UnauthorizedException("Only project admin can delete");
        }

        projectRepository.delete(project);
    }

    @Override
    @Transactional
    public ProjectDto addMember(Long projectId, AddMemberRequest request, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!isProjectAdmin(project, userEmail)) {
            throw new UnauthorizedException("Only project admin can add members");
        }

        User newMember = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, newMember.getId())) {
            throw new BadRequestException("User is already a member of this project");
        }

        ProjectMember member = ProjectMember.builder()
                .project(project)
                .user(newMember)
                .role(Role.MEMBER)
                .build();

        projectMemberRepository.save(member);
        return mapToProjectDto(project);
    }

    @Override
    @Transactional
    public void removeMember(Long projectId, Long memberId, String userEmail) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!isProjectAdmin(project, userEmail)) {
            throw new UnauthorizedException("Only project admin can remove members");
        }

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (member.getUser().getId().equals(project.getCreatedBy().getId())) {
            throw new BadRequestException("Cannot remove project owner");
        }

        projectMemberRepository.delete(member);
    }

    private boolean isMemberOrAdmin(Project project, String email) {
        return project.getMembers().stream()
                .anyMatch(m -> m.getUser().getEmail().equals(email))
                || project.getCreatedBy().getEmail().equals(email);
    }

    private boolean isProjectAdmin(Project project, String email) {
        return project.getMembers().stream()
                .filter(m -> m.getRole() == Role.ADMIN)
                .anyMatch(m -> m.getUser().getEmail().equals(email))
                || project.getCreatedBy().getEmail().equals(email);
    }

    private ProjectDto mapToProjectDto(Project project) {
//        long totalTasks = taskRepository.countByProjectIdAndStatus(project.getId(), null) +
//                         taskRepository.findByProjectId(project.getId()).size();
        long totalTasks = taskRepository.countByProjectId(project.getId());
        long completedTasks = taskRepository.countByProjectIdAndStatus(project.getId(), TaskStatus.DONE);
        if (totalTasks == 0) {
            List<com.taskmanager.entity.Task> tasks = taskRepository.findByProjectId(project.getId());
            totalTasks = tasks.size();
        }
     //   long completedTasks = taskRepository.countByProjectIdAndStatus(project.getId(), com.taskmanager.enums.TaskStatus.DONE);
        double progress = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0;

        Set<ProjectMemberDto> members = project.getMembers().stream()
                .map(m -> ProjectMemberDto.builder()
                        .id(m.getId())
                        .projectId(project.getId())
                        .user(mapToUserSummary(m.getUser()))
                        .role(m.getRole())
                        .joinedAt(m.getJoinedAt())
                        .build())
                .collect(Collectors.toSet());

        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
//                .startDate(project.getStartDate().format(DATE_FMT))
//                .endDate(project.getEndDate().format(DATE_FMT))

                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .active(project.isActive())
                .createdAt(project.getCreatedAt())
                .createdBy(mapToUserSummary(project.getCreatedBy()))
                .members(members)
                .totalTasks((int) totalTasks)
                .completedTasks((int) completedTasks)
                .progressPercentage(Math.round(progress * 100.0) / 100.0)
                .build();
    }

    private UserSummaryDto mapToUserSummary(User user) {
        if (user == null) return null;
        return UserSummaryDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .build();
    }
}
