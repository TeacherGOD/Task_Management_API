package com.example.taskmanagement.security.expressions;

import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.exception.AccessDeniedException;
import com.example.taskmanagement.exception.CommentNotFoundException;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("commentSecurity")
@RequiredArgsConstructor
public class CommentSecurity {

    private final CommentRepository commentRepository;

    public boolean isCommentAuthorOrAdmin(Long commentId, UserDetailsImpl userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!(userDetails.isAdmin() || comment.getAuthor().getId().equals(userDetails.getId()))) {
            throw new AccessDeniedException("Cannot interact with others' comments");
        }
        return true;
    }
}