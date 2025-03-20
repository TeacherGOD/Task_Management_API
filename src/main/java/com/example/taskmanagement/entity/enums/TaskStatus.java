package com.example.taskmanagement.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum TaskStatus {
    @Schema(description = "В ожидании", example = "PENDING")
    PENDING,
    @Schema(description = "В процессе", example = "IN_PROGRESS")
    IN_PROGRESS,
    @Schema(description = "Завершено", example = "COMPLETED")
    COMPLETED
}