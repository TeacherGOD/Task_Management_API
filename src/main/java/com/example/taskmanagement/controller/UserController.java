package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.response.UserDto;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import com.example.taskmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/me")
    @Operation(summary = "Получить текущего пользователя")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public UserDto getCurrentUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return modelMapper.map(userDetails.getUser(), UserDto.class);
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей (только для админов)")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по ID")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isSelf(#id, principal)")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}