package com.example.taskmanagement.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Schema(description = "Ответ об ошибке")
public class ErrorResponse {
    @Schema(description = "Временная метка", example = "2023-10-05T12:34:56Z")
    private Instant timestamp;

    @Schema(description = "HTTP статус код", example = "404")
    private int status;

    @Schema(description = "Тип ошибки", example = "Not Found")
    private String error;

    @Schema(description = "Сообщение об ошибке", example = "Ресурс не найден")
    private String message;

    @Schema(description = "Путь запроса", example = "/api/tasks/99")
    private String path;

    public ErrorResponse(String message) {
        this.message = message;
    }
}