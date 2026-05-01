package com.taskmanager.controller;

import com.taskmanager.dto.DashboardStatsDto;
import com.taskmanager.dto.TaskDto;
import com.taskmanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDto> getStats() {
        return ResponseEntity.ok(dashboardService.getStats(getCurrentEmail()));
    }

    @GetMapping("/recent-tasks")
    public ResponseEntity<List<TaskDto>> getRecentTasks() {
        return ResponseEntity.ok(dashboardService.getRecentTasks(getCurrentEmail()));
    }

    @GetMapping("/upcoming-tasks")
    public ResponseEntity<List<TaskDto>> getUpcomingTasks() {
        return ResponseEntity.ok(dashboardService.getUpcomingTasks(getCurrentEmail()));
    }

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
