package com.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalProjects;
    private long totalTasks;
    private long completedTasks;
    private long inProgressTasks;
    private long todoTasks;
    private long overdueTasks;
    private double completionRate;
}
