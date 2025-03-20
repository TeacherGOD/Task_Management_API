package com.example.taskmanagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO для аутентификации")
public class AuthRequestDto {
    @Schema(description = "Email пользователя", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email
    private String email;

    @Schema(description = "Пароль", example = "myPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String password;

}