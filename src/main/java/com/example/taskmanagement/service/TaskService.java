package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.TaskCreateDto;
import com.example.taskmanagement.dto.request.TaskUpdateDto;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.entity.enums.TaskPriority;
import com.example.taskmanagement.entity.enums.TaskStatus;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.exception.UserNotFoundException;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.taskmanagement.util.SecurityUtils.isNotAdmin;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Task createTask(TaskCreateDto dto, User authenticatedUser) {

        Task task = modelMapper.map(dto, Task.class);
        task.setAuthor(authenticatedUser);

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new UserNotFoundException(dto.getAssigneeId()));
            task.setAssignee(assignee);
        }
        task.setStatus(TaskStatus.PENDING);
        log.info("Creating task by user: {}", authenticatedUser.getId());
        return taskRepository.save(task);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isTaskOwner(#id, principal)")
    public Task updateTask(Long taskId, TaskUpdateDto dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));


        modelMapper.map(dto, task);

        if (dto.getAssigneeId() != null) {
            User assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new UserNotFoundException(dto.getAssigneeId()));
            task.setAssignee(assignee);
        }else {
            task.setAssignee(null);
        }

        return taskRepository.save(task);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isTaskOwner(#id, principal)")
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        taskRepository.delete(task);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
    }


    public Page<Task> getTasks(
            Long authorId,
            Long assigneeId,
            TaskStatus status,
            TaskPriority priority,
            Pageable pageable,
            User authenticatedUser
    ) {
         if (isNotAdmin(authenticatedUser)) {
            return taskRepository.findByAuthorIdOrAssigneeId(
                    authenticatedUser.getId(),
                    authenticatedUser.getId(),
                    pageable
            );
        }

        return taskRepository.findWithFilters(
                authorId,
                assigneeId,
                status,
                priority,
                pageable
        );
    }


    @Transactional
    public Task changeStatus(Long taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

}