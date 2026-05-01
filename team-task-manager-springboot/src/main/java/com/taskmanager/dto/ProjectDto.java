package com.taskmanager.dto;
import java.time.LocalDate;
import com.taskmanager.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private LocalDateTime createdAt;
    private UserSummaryDto createdBy;
    private Set<ProjectMemberDto> members;
    private int totalTasks;
    private int completedTasks;
    private double progressPercentage;
}
