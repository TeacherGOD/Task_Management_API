package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.TaskCreateDto;
import com.example.taskmanagement.dto.request.TaskUpdateDto;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.entity.enums.TaskPriority;
import com.example.taskmanagement.entity.enums.TaskStatus;
import com.example.taskmanagement.entity.enums.UserRole;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.exception.UserNotFoundException;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class TaskServiceTest {
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private TaskService taskService;


    @Test
    @WithMockUser(roles = "ADMIN")
    void createTask_ShouldSetAuthorAndSave() {
        User author = User.builder().id(99L).role(UserRole.ROLE_ADMIN).build();
        TaskCreateDto dto = new TaskCreateDto("Title", "Desc", TaskPriority.HIGH, null);
        Task mockTask = new Task();

        when(modelMapper.map(dto, Task.class)).thenReturn(mockTask);
        when(taskRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.createTask(dto, author);

        assertEquals(author, result.getAuthor());
        verify(taskRepository).save(mockTask);
    }

    @Test
    void updateTask_WhenTaskNotFound_ShouldThrowException() {
        Long taskId = 999L;
        TaskUpdateDto dto = new TaskUpdateDto("Task 1", "Desc 1",TaskPriority.HIGH,TaskStatus.PENDING , null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(taskId, dto));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteTask_WhenTaskExists_ShouldCallRepository() {
        Long taskId = 1L;
        Task mockTask = new Task();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));
        taskService.deleteTask(taskId);

        verify(taskRepository).delete(mockTask);
    }

    @WithMockUser(roles ="ADMIN")
    @Test
    void updateTask_WhenAssigneeNotFound_ShouldThrowException() {
        Long taskId = 1L;
        Long assigneeId = 999L;
        TaskUpdateDto dto = new TaskUpdateDto("title", "desc", TaskPriority.HIGH, TaskStatus.IN_PROGRESS, assigneeId);

        Task mockTask = new Task();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(mockTask));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> taskService.updateTask(taskId, dto));
    }
}