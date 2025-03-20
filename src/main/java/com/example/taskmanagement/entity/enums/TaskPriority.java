package com.example.taskmanagement.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum TaskPriority {
    @Schema(description = "Высокий", example = "HIGH")
    HIGH,
    @Schema(description = "Средний", example = "MEDIUM")
    MEDIUM,
    @Schema(description = "Низкий", example = "LOW")
    LOW
}