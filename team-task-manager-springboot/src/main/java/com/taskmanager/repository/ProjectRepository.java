package com.taskmanager.repository;

import com.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByCreatedById(Long userId);

    @Query("SELECT p FROM Project p " +
           "LEFT JOIN FETCH p.members m " +
           "LEFT JOIN FETCH m.user " +
           "WHERE p.id = :id")
    Project findByIdWithMembers(@Param("id") Long id);

    @Query("SELECT DISTINCT p FROM Project p " +
           "LEFT JOIN p.members m " +
           "WHERE p.createdBy.id = :userId OR m.user.id = :userId")
    List<Project> findByUserId(@Param("userId") Long userId);

    long countByActiveTrue();
}
