package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.request.TaskCreateDto;
import com.example.taskmanagement.dto.request.TaskUpdateDto;
import com.example.taskmanagement.dto.response.ErrorResponse;
import com.example.taskmanagement.dto.response.TaskResponseDto;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.enums.TaskPriority;
import com.example.taskmanagement.entity.enums.TaskStatus;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import com.example.taskmanagement.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(
        name = "Task Management",
        description = "API для управления задачами"
)
public class TaskController {

    private final TaskService taskService;
    private final ModelMapper modelMapper;


    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Создать новую задачу",
            description = "Доступно для авторизованных пользователей. Автор задачи назначается автоматически"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "201",
            description = "Задача успешно создана",
            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные в запросе",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Исполнитель не найден (если указан assigneeId)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public TaskResponseDto createTask(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания задачи",
                    content = @Content(schema = @Schema(implementation = TaskCreateDto.class))
            )
            TaskCreateDto dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Task task = taskService.createTask(dto, userDetails.getUser());
        return modelMapper.map(task, TaskResponseDto.class);
    }


    @Operation(
            summary = "Обновить задачу",
            description = "Доступно администраторам или автору задачи"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Задача обновлена",
            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Нет прав для редактирования задачи",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Задача или исполнитель не найдены",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isTaskOwner(#id, principal)")
    public TaskResponseDto updateTask(
            @Parameter(description = "ID задачи", example = "2")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для обновления задачи",
                    content = @Content(schema = @Schema(implementation = TaskUpdateDto.class))
            )
            @Valid @RequestBody TaskUpdateDto dto
    ) {
        Task task = taskService.updateTask(id, dto);
        return modelMapper.map(task, TaskResponseDto.class);
    }


    @Operation(
            summary = "Получить задачу по ID",
            description = "Доступно автору, исполнителю или администратору"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос",
            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Задача не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @GetMapping("/{id}")
    @PreAuthorize("@taskSecurity.canAccessTask(#id, principal)")
    public TaskResponseDto getTask(
            @Parameter(description = "ID задачи", example = "2")
            @PathVariable Long id) {
        return modelMapper.map(taskService.getTaskById(id), TaskResponseDto.class);
    }


    @Operation(
            summary = "Получить задачи с фильтрацией",
            description = "Фильтрация по автору, исполнителю, статусу и приоритету. Пагинация включена."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TaskResponseDto.class)))
    )
    @GetMapping
    public Page<TaskResponseDto> getTasks(
            @Parameter(description = "ID автора задачи", example = "1")
            @RequestParam(required = false) Long authorId,

            @Parameter(description = "ID исполнителя задачи", example = "2")
            @RequestParam(required = false) Long assigneeId,

            @Parameter(description = "Статус задачи", examples = {
                    @ExampleObject(name = "В ожидании", value = "PENDING"),
                    @ExampleObject(name = "В процессе", value = "IN_PROGRESS"),
                    @ExampleObject(name = "Завершен", value = "COMPLETED"),
                    @ExampleObject(name = "Любая", value = "-")
            })
            @RequestParam(required = false) TaskStatus status,

            @Parameter(description = "Приоритет задачи", examples = {
                    @ExampleObject(name = "Высокий", value = "HIGH"),
                    @ExampleObject(name = "Средний", value = "MEDIUM"),
                    @ExampleObject(name = "Низкий", value = "LOW")
            })
            @RequestParam(required = false) TaskPriority priority,
            @Parameter(
                    description = "Параметры пагинации"
            )
            @PageableDefault Pageable pageable,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return taskService.getTasks(authorId, assigneeId, status, priority, pageable,userDetails.getUser())
                .map(task -> modelMapper.map(task, TaskResponseDto.class));
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить задачу",
            description = "Доступно администраторам или автору задачи"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Задача удалена")
    @ApiResponse(
            responseCode = "403",
            description = "Нет прав для удаления задачи",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskSecurity.isTaskOwner(#id, principal)")
    public void deleteTask(
            @Parameter(description = "ID задачи", example = "2")
            @PathVariable Long id) {
        taskService.deleteTask(id);
    }


    @Operation(
            summary = "Изменить статус задачи",
            description = "Доступно исполнителю задачи или администратору"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}/status")
    @PreAuthorize("@taskSecurity.isAssigneeOrAdmin(#id, principal)")
    @ApiResponse(
            responseCode = "200",
            description = "Статус обновлен",
            content = @Content(schema = @Schema(implementation = TaskResponseDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Нет прав для изменения статуса",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    public TaskResponseDto updateStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status
    ) {
        return modelMapper.map(taskService.changeStatus(id, status), TaskResponseDto.class);
    }
}