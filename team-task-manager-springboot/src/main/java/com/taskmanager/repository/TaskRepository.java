package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import com.taskmanager.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssigneeId(Long assigneeId);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.assignee.id = :userId AND t.status <> 'DONE' AND t.status <> 'CANCELLED' AND t.dueDate < :today")
    List<Task> findOverdueTasksByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.status <> 'DONE' AND t.status <> 'CANCELLED' AND t.dueDate < :today")
    List<Task> findAllOverdueTasks(@Param("today") LocalDate today);

    long countByStatus(TaskStatus status);

    long countByStatusNotIn(List<TaskStatus> statuses);

    @Query("SELECT t.project.id, COUNT(t) FROM Task t WHERE t.status = 'DONE' GROUP BY t.project.id")
    List<Object[]> countCompletedTasksByProject();

    long countByProjectId(Long id);
}
