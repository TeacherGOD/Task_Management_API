package com.example.taskmanagement.dto.request;

import com.example.taskmanagement.entity.enums.TaskPriority;
import com.example.taskmanagement.entity.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для обновления задачи")
public class TaskUpdateDto {
    @Schema(description = "Новый заголовок", example = "Обновленный заголовок", maxLength = 200)
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Schema(description = "Новое описание", example = "Исправлены все баги", maxLength = 2000)
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Schema(description = "Новый приоритет", example = "MEDIUM")
    private TaskPriority priority;

    @Schema(description = "Новый статус", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "ID нового исполнителя", example = "20")
    private Long assigneeId;

    public TaskUpdateDto(String title, String description, TaskPriority priority, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assigneeId = null;
    }
}