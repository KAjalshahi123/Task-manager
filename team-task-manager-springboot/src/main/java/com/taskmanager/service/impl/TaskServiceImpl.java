package com.taskmanager.service.impl;

import com.taskmanager.dto.*;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.ProjectMember;
import com.taskmanager.entity.Task;
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
import com.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    @Transactional
    public TaskDto createTask(Long projectId, TaskRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (!isProjectMember(projectId, user.getId()) && !project.getCreatedBy().getEmail().equals(userEmail)) {
            throw new UnauthorizedException("You are not a member of this project");
        }

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            if (!isProjectMember(projectId, assignee.getId())) {
                throw new BadRequestException("Assignee is not a project member");
            }
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : com.taskmanager.enums.TaskPriority.MEDIUM)
                .dueDate(request.getDueDate())
                .project(project)
                .assignee(assignee)
                .createdBy(user)
                .build();

        return mapToTaskDto(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!canAccessTask(task, userEmail)) {
            throw new UnauthorizedException("Access denied");
        }
        return mapToTaskDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByProject(Long projectId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!isProjectMember(projectId, user.getId()) && !isProjectOwner(projectId, userEmail)) {
            throw new UnauthorizedException("Access denied");
        }
        return taskRepository.findByProjectId(projectId).stream()
                .map(this::mapToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getMyTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return taskRepository.findByAssigneeId(user.getId()).stream()
                .map(this::mapToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskDto updateTask(Long id, TaskRequest request, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!canModifyTask(task, userEmail)) {
            throw new UnauthorizedException("You cannot modify this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            if (!isProjectMember(task.getProject().getId(), assignee.getId())) {
                throw new BadRequestException("Assignee is not a project member");
            }
            task.setAssignee(assignee);
        }

        return mapToTaskDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDto updateTaskStatus(Long id, TaskStatusUpdateRequest request, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!canAccessTask(task, userEmail)) {
            throw new UnauthorizedException("Access denied");
        }

        task.setStatus(request.getStatus());
        if (request.getStatus() == TaskStatus.DONE) {
            task.setCompletedDate(LocalDate.now());
        } else {
            task.setCompletedDate(null);
        }

        return mapToTaskDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public void deleteTask(Long id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (!canModifyTask(task, userEmail)) {
            throw new UnauthorizedException("You cannot delete this task");
        }
        taskRepository.delete(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getOverdueTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() == Role.ADMIN) {
            return taskRepository.findAllOverdueTasks(LocalDate.now()).stream()
                    .map(this::mapToTaskDto)
                    .collect(Collectors.toList());
        }
        return taskRepository.findOverdueTasksByUserId(user.getId(), LocalDate.now()).stream()
                .map(this::mapToTaskDto)
                .collect(Collectors.toList());
    }

    private boolean isProjectMember(Long projectId, Long userId) {
        return projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    private boolean isProjectOwner(Long projectId, String email) {
        Project project = projectRepository.findById(projectId).orElse(null);
        return project != null && project.getCreatedBy().getEmail().equals(email);
    }

    private boolean canAccessTask(Task task, String email) {
        return isProjectMember(task.getProject().getId(), 
                userRepository.findByEmail(email).map(User::getId).orElse(0L))
                || task.getCreatedBy().getEmail().equals(email)
                || task.getProject().getCreatedBy().getEmail().equals(email);
    }

    private boolean canModifyTask(Task task, String email) {
        return task.getCreatedBy().getEmail().equals(email)
                || task.getProject().getCreatedBy().getEmail().equals(email)
                || isProjectAdmin(task.getProject().getId(), email);
    }

    private boolean isProjectAdmin(Long projectId, String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return false;
        return projectMemberRepository.findByProjectIdAndUserId(projectId, user.getId())
                .map(ProjectMember::getRole)
                .filter(role -> role == Role.ADMIN)
                .isPresent();
    }

    private TaskDto mapToTaskDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate() != null ? task.getDueDate().format(DATE_FMT) : null)
                .completedDate(task.getCompletedDate() != null ? task.getCompletedDate().format(DATE_FMT) : null)
                .overdue(task.isOverdue())
                .createdAt(task.getCreatedAt())
                .assignee(mapToUserSummary(task.getAssignee()))
                .createdBy(mapToUserSummary(task.getCreatedBy()))
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
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
