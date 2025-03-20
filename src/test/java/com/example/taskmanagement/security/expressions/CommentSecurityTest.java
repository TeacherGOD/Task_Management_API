package com.example.taskmanagement.security.expressions;

import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.entity.enums.UserRole;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentSecurityTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentSecurity commentSecurity;

    @Test
    void isCommentAuthorOrAdmin_WhenUserIsAuthor_ShouldReturnTrue() {
        Long commentId = 1L;
        User author = User.builder().id(99L).build();
        Comment comment = Comment.builder().author(author).build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        UserDetailsImpl userDetails = new UserDetailsImpl(author);
        assertTrue(commentSecurity.isCommentAuthorOrAdmin(commentId, userDetails));
    }

    @Test
    void isCommentAuthorOrAdmin_WhenUserIsAdmin_ShouldReturnTrue() {
        Long commentId = 1L;
        User admin = User.builder().id(100L).role(UserRole.ROLE_ADMIN).build();
        Comment comment = Comment.builder().author(User.builder().id(99L).build()).build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        UserDetailsImpl userDetails = new UserDetailsImpl(admin);
        assertTrue(commentSecurity.isCommentAuthorOrAdmin(commentId, userDetails));
    }
}