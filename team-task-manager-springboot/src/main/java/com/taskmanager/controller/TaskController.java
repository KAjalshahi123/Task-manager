package com.taskmanager.controller;

import com.taskmanager.dto.*;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/project/{projectId}")
    public ResponseEntity<TaskDto> createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(projectId, request, getCurrentEmail()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id, getCurrentEmail()));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDto>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProject(projectId, getCurrentEmail()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<TaskDto>> getMyTasks() {
        return ResponseEntity.ok(taskService.getMyTasks(getCurrentEmail()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request, getCurrentEmail()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(@PathVariable Long id, @RequestBody TaskStatusUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateTaskStatus(id, request, getCurrentEmail()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id, getCurrentEmail());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks(getCurrentEmail()));
    }

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
