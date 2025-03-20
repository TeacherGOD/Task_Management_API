package com.example.taskmanagement.dto.response;


import com.example.taskmanagement.entity.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для представления пользователя")
public class UserDto {
    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Email", example = "user@example.com")
    private String email;

    @Schema(description = "Полное имя", example = "Алексей Петров")
    private String fullName;

    @Schema(description = "Роль пользователя", example = "USER")
    private UserRole role;

    @Schema(description = "Дата регистрации", example = "2023-01-15T08:00:00")
    private LocalDateTime createdAt;

}