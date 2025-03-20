package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.CommentCreateDto;
import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.exception.CommentMismatchException;
import com.example.taskmanagement.exception.CommentNotFoundException;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public Comment addComment(Long taskId, CommentCreateDto dto, User author) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .task(task)
                .author(author)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional
    @PreAuthorize("@commentSecurity.isCommentAuthorOrAdmin(#commentId, principal)")
    public void deleteComment(Long taskId,Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException(taskId));
        if (task.getComments().stream().noneMatch(comm->comm.equals(comment)))
            throw new CommentMismatchException(taskId, commentId);
        commentRepository.delete(comment);
    }


    public Page<Comment> getCommentsForTask(Long taskId, Pageable pageable) {
        return commentRepository.findByTaskId(taskId, pageable);
    }
}