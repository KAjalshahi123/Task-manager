package com.taskmanager.service;

import com.taskmanager.dto.*;

import java.util.List;

public interface TaskService {
    TaskDto createTask(Long projectId, TaskRequest request, String userEmail);
    TaskDto getTaskById(Long id, String userEmail);
    List<TaskDto> getTasksByProject(Long projectId, String userEmail);
    List<TaskDto> getMyTasks(String userEmail);
    TaskDto updateTask(Long id, TaskRequest request, String userEmail);
    TaskDto updateTaskStatus(Long id, TaskStatusUpdateRequest request, String userEmail);
    void deleteTask(Long id, String userEmail);
    List<TaskDto> getOverdueTasks(String userEmail);
}
