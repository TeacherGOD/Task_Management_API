package com.example.taskmanagement.dto.response;

import com.example.taskmanagement.dto.response.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для представления комментария")
public class CommentDto {
    @Schema(description = "ID комментария", example = "1")
    private Long id;

    @Schema(description = "Содержание комментария", example = "Все задачи выполнены")
    private String content;

    @Schema(description = "Автор комментария")
    private UserDto author;

    @Schema(description = "Дата создания комментария", example = "2023-10-05T14:30:00")
    private LocalDateTime createdAt;
}