package com.example.taskmanagement.security.expressions;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.exception.AccessDeniedException;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
@RequiredArgsConstructor
public class TaskSecurity {

    private final TaskRepository taskRepository;

    public boolean isTaskOwner(Long taskId, UserDetailsImpl userDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        if (!(task.getAuthor().getId().equals(userDetails.getId()))) {
            throw new AccessDeniedException("You are not the task owner");
        }
        return true;
    }

    public boolean canAccessTask(Long taskId, UserDetailsImpl userDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        if (!(userDetails.isAdmin()
                || task.getAuthor().getId().equals(userDetails.getId())
                || task.getAssignee().getId().equals(userDetails.getId()))){
            throw new AccessDeniedException("No access to this task");
        }
        return true;
    }

    public boolean isAssigneeOrAdmin(Long taskId, UserDetailsImpl userDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        if (
                userDetails.isNotAdmin()
                && !task.getAssignee().getId().equals(userDetails.getId())){
            throw new AccessDeniedException("Only assignee can change status");
        }
        return true;
    }
}
