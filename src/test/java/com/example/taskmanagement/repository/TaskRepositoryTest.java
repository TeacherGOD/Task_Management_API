package com.example.taskmanagement.repository;


import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void findByStatus_ShouldReturnFilteredTasks() {
        var tasks = taskRepository.findByStatus(TaskStatus.PENDING);

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> t.getStatus() == TaskStatus.PENDING));
    }

    @Test
    void findByAuthorId_ShouldReturnTasks() {
        Page<Task> tasks = taskRepository.findByAuthorId(1L, Pageable.unpaged());
        assertEquals(3, tasks.getTotalElements());
    }
}