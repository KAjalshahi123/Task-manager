package com.taskmanager.service;

import com.taskmanager.dto.*;

import java.util.List;

public interface ProjectService {
    ProjectDto createProject(ProjectRequest request, String userEmail);
    ProjectDto getProjectById(Long id, String userEmail);
    List<ProjectDto> getMyProjects(String userEmail);
    List<ProjectDto> getAllProjects();
    ProjectDto updateProject(Long id, ProjectRequest request, String userEmail);
    void deleteProject(Long id, String userEmail);
    ProjectDto addMember(Long projectId, AddMemberRequest request, String userEmail);
    void removeMember(Long projectId, Long memberId, String userEmail);
}
