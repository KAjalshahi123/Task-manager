package com.taskmanager.dto;

import com.taskmanager.enums.TaskPriority;
import com.taskmanager.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private String dueDate;
    private String completedDate;
    private boolean overdue;
    private LocalDateTime createdAt;
    private UserSummaryDto assignee;
    private UserSummaryDto createdBy;
    private Long projectId;
    private String projectName;
}
