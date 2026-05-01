package com.taskmanager.service;

import com.taskmanager.dto.DashboardStatsDto;
import com.taskmanager.dto.TaskDto;

import java.util.List;

public interface DashboardService {
    DashboardStatsDto getStats(String userEmail);
    List<TaskDto> getRecentTasks(String userEmail);
    List<TaskDto> getUpcomingTasks(String userEmail);
}
