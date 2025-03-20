package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.request.AuthRequestDto;
import com.example.taskmanagement.dto.request.UserRegistrationDto;
import com.example.taskmanagement.dto.response.AuthResponseDto;
import com.example.taskmanagement.dto.response.ErrorResponse;
import com.example.taskmanagement.dto.response.UserDto;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.security.jwt.JwtTokenProvider;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import com.example.taskmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Аутентификация и регистрация",
        description = "API для входа в систему и регистрации новых пользователей"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Operation(
            summary = "Аутентификация пользователя",
            description = "Возвращает JWT токен и данные пользователя для авторизованного доступа"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешная аутентификация",
            content = @Content(schema = @Schema(implementation = AuthResponseDto.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Неверные email/пароль",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDto> authenticateUser(
            @Valid @RequestBody AuthRequestDto request
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserDto userDto = modelMapper.map(userDetails.getUser(),UserDto.class);


        return ResponseEntity.ok(new AuthResponseDto(jwt, userDto));
    }

    @Operation(
            summary = "Регистрация нового пользователя",
            description = "Создает учетную запись пользователя с ролью ROLE_USER"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно зарегистрирован",
            content = @Content(schema = @Schema(implementation = UserDto.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Пользователь с таким email уже существует",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @ApiResponse(
            responseCode = "400",
            description = "Некорректные данные в запросе",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
    )
    @PostMapping("/signup")
    public ResponseEntity<UserDto> registerUser(
            @Valid @RequestBody UserRegistrationDto dto
    ) {
        User user = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(user,UserDto.class));
    }
}