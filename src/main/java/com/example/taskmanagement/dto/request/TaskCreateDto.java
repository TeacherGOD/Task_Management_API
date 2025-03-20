package com.example.taskmanagement.dto.request;

import com.example.taskmanagement.entity.enums.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Данные для создания задачи")
public class TaskCreateDto {
    @Schema(description = "Заголовок задачи", example = "Исправить баг", maxLength = 200, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message="Заголовок обязателен")
    @Size(max = 200,message = "Максимальная длина заголовка — 200 символов")
    private String title;

    @Schema(description = "Описание задачи", example = "Пользователи не могут войти с Google аккаунтом", maxLength = 2000)
    @Size(max = 2000, message = "Максимальная длина описания — 1000 символов")
    private String description;

    @Schema(description = "Приоритет задачи", example = "HIGH", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Приоритет обязателен")
    private TaskPriority priority;

    @Schema(description = "ID исполнителя (опционально)", example = "15")
    private Long assigneeId;

}