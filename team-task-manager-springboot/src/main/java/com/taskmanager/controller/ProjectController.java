package com.taskmanager.controller;

import com.taskmanager.dto.*;
import com.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.createProject(request, getCurrentEmail()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id, getCurrentEmail()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProjectDto>> getMyProjects() {
        return ResponseEntity.ok(projectService.getMyProjects(getCurrentEmail()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request, getCurrentEmail()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id, getCurrentEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<ProjectDto> addMember(@PathVariable Long projectId, @RequestBody AddMemberRequest request) {
        return ResponseEntity.ok(projectService.addMember(projectId, request, getCurrentEmail()));
    }

    @DeleteMapping("/{projectId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(@PathVariable Long projectId, @PathVariable Long memberId) {
        projectService.removeMember(projectId, memberId, getCurrentEmail());
        return ResponseEntity.noContent().build();
    }

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
