package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.UserRegistrationDto;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.entity.enums.UserRole;
import com.example.taskmanagement.exception.AccessDeniedException;
import com.example.taskmanagement.exception.UserNotFoundException;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class UserServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;


    @Autowired
    private UserService userService;

    @Test
    void registerUser_WhenEmailNotExists_ShouldReturnUser() {
        UserRegistrationDto dto = new UserRegistrationDto(
                "test@mail.com",
                "password",
                "Test User"
        );

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("test@mail.com", result.getEmail());
        assertEquals(UserRole.ROLE_USER, result.getRole());

        verify(userRepository).save(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WhenAdminAndUserExists_ShouldDelete() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        userService.deleteUser(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WhenAdminAndUserNotExists_ShouldThrowException() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteUser_WhenNotAdmin_ShouldDenyAccess() {
        assertThrows(AuthorizationDeniedException.class, () -> userService.deleteUser(2L));
    }
}