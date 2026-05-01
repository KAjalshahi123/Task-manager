package com.taskmanager.service.impl;

import com.taskmanager.dto.DashboardStatsDto;
import com.taskmanager.dto.TaskDto;
import com.taskmanager.dto.UserSummaryDto;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.enums.Role;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getStats(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == Role.ADMIN;
        long totalProjects = isAdmin ? projectRepository.countByActiveTrue() : 0;
        long totalTasks, completedTasks, inProgressTasks, todoTasks, overdueTasks;

        if (isAdmin) {
            totalTasks = taskRepository.count();
            completedTasks = taskRepository.countByStatus(TaskStatus.DONE);
            inProgressTasks = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
            todoTasks = taskRepository.countByStatus(TaskStatus.TODO);
            overdueTasks = taskRepository.findAllOverdueTasks(LocalDate.now()).size();
        } else {
            List<Task> myTasks = taskRepository.findByAssigneeId(user.getId());
            totalTasks = myTasks.size();
            completedTasks = myTasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();
            inProgressTasks = myTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
            todoTasks = myTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
            overdueTasks = myTasks.stream().filter(Task::isOverdue).count();
        }

        double completionRate = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0;

        return DashboardStatsDto.builder()
                .totalProjects(totalProjects)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .todoTasks(todoTasks)
                .overdueTasks(overdueTasks)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getRecentTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Task> tasks;
        if (user.getRole() == Role.ADMIN) {
            tasks = taskRepository.findAll();
        } else {
            tasks = taskRepository.findByAssigneeId(user.getId());
        }

        return tasks.stream()
                .sorted(Comparator.comparing(Task::getCreatedAt).reversed())
                .limit(10)
                .map(this::mapToTaskDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getUpcomingTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Task> tasks;
        if (user.getRole() == Role.ADMIN) {
            tasks = taskRepository.findAll().stream()
                    .filter(t -> t.getStatus() != TaskStatus.DONE && t.getStatus() != TaskStatus.CANCELLED)
                    .collect(Collectors.toList());
        } else {
            tasks = taskRepository.findByAssigneeId(user.getId()).stream()
                    .filter(t -> t.getStatus() != TaskStatus.DONE && t.getStatus() != TaskStatus.CANCELLED)
                    .collect(Collectors.toList());
        }

        return tasks.stream()
                .sorted(Comparator.comparing(Task::getDueDate))
                .limit(10)
                .map(this::mapToTaskDto)
                .collect(Collectors.toList());
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
