package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.entity.enums.TaskPriority;
import com.example.taskmanagement.entity.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    Page<Task> findByAuthorId(Long authorId, Pageable pageable);

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE " +
            "(:authorId IS NULL OR t.author.id = :authorId) AND " +
            "(:assigneeId IS NULL OR t.assignee.id = :assigneeId) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:priority IS NULL OR t.priority = :priority)")
    Page<Task> findWithFilters(
            @Param("authorId") Long authorId,
            @Param("assigneeId") Long assigneeId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            Pageable pageable
    );

    Page<Task> findByAuthorIdOrAssigneeId(Long id, Long id1, Pageable pageable);

    List<Task> findByStatus(TaskStatus taskStatus);
}