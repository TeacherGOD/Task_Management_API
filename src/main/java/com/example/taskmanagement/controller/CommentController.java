package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.request.CommentCreateDto;
import com.example.taskmanagement.dto.response.CommentDto;
import com.example.taskmanagement.dto.response.ErrorResponse;
import com.example.taskmanagement.entity.Comment;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import com.example.taskmanagement.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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
@RequestMapping("/api/tasks/{taskId}/comments")
@RequiredArgsConstructor
@Tag(
        name = "Comment Management",
        description = "API для управления комментариями"
)
public class CommentController {

    private final CommentService commentService;
    private final ModelMapper modelMapper;


    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Добавить комментарий",
            description = "Доступно для авторизованных пользователей. Комментарий привязывается к задаче"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(
            responseCode = "201",
            description = "Комментарий успешно создан",
            content = @Content(schema = @Schema(implementation = CommentDto.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Пользователь не авторизован",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Задача не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping
    public CommentDto addComment(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable
                Long taskId,
            @Valid
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания комментария",
                    content = @Content(schema = @Schema(implementation = CommentCreateDto.class))
            )
                CommentCreateDto dto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Comment comment = commentService.addComment(taskId, dto, userDetails.getUser());
        return modelMapper.map(comment, CommentDto.class);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Удалить комментарий",
            description = "Доступно только автору комментария или администратору"
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponse(responseCode = "204", description = "Комментарий удален")
    @ApiResponse(
            responseCode = "403",
            description = "Нет прав для удаления комментария",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Комментарий не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Неверные данные для удаления комментария",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @DeleteMapping("/{commentId}")
    @PreAuthorize("@commentSecurity.isCommentAuthorOrAdmin(#commentId, principal)")
    public void deleteComment(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long taskId,
            @Parameter(description = "ID комментария", example = "5")
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(taskId,commentId);
    }

    @GetMapping
    @Operation(
            summary = "Получить комментарии задачи",
            description = "Возвращает список комментариев с пагинацией"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешный запрос",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CommentDto.class)))
    )
    public Page<CommentDto> getComments(
            @PathVariable Long taskId,
            @PageableDefault Pageable pageable
    ) {
        return commentService.getCommentsForTask(taskId, pageable)
                .map(comment -> modelMapper.map(comment, CommentDto.class));
    }
}