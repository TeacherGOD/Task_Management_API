package com.example.taskmanagement.dto.response;



import com.example.taskmanagement.entity.enums.TaskPriority;
import com.example.taskmanagement.entity.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ с данными задачи")
public class TaskResponseDto {
    @Schema(description = "ID задачи", example = "42")
    private Long id;

    @Schema(description = "Заголовок", example = "Рефакторинг кода")
    private String title;

    @Schema(description = "Описание", example = "Улучшить читаемость кода")
    private String description;

    @Schema(description = "Статус", example = "COMPLETED")
    private TaskStatus status;

    @Schema(description = "Приоритет", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Автор задачи")
    private UserDto author;

    @Schema(description = "Исполнитель задачи")
    private UserDto assignee;

    @Schema(description = "Список комментариев")
    private List<CommentDto> comments;

    @Schema(description = "Дата создания", example = "2023-09-20T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Дата обновления", example = "2023-09-21T11:20:45")
    private LocalDateTime updatedAt;
}
