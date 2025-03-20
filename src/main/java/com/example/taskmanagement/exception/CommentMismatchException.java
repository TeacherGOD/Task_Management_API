package com.example.taskmanagement.exception;

public class CommentMismatchException extends RuntimeException {
    public CommentMismatchException(Long commentId, Long topicId) {
        super(String.format("Comment with ID %d does not belong to topic with ID %d.", commentId, topicId));
    }
}