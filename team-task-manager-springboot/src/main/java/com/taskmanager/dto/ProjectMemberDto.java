package com.taskmanager.dto;

import com.taskmanager.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberDto {
    private Long id;
    private Long projectId;
    private UserSummaryDto user;
    private Role role;
    private LocalDateTime joinedAt;
}
