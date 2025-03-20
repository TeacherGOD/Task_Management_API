package com.example.taskmanagement.controller;

import com.example.taskmanagement.config.ModelMapperConfig;
import com.example.taskmanagement.config.SecurityConfig;
import com.example.taskmanagement.dto.response.UserDto;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.entity.enums.UserRole;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.expressions.UserSecurity;
import com.example.taskmanagement.security.handlers.AuthEntryPointJwt;
import com.example.taskmanagement.security.jwt.JwtTokenProvider;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import com.example.taskmanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({
        SecurityConfig.class,
        UserSecurity.class,
        ModelMapperConfig.class
})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @Mock
    private ModelMapper modelMapper;

    @Test
    @WithMockUser(roles = "USER")
    void getCurrentUser_Authenticated_ShouldReturnUser() throws Exception {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .password("password")
                .role(UserRole.ROLE_USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        UserDto expectedDto = new UserDto();
        expectedDto.setEmail("test@mail.com");
        when(modelMapper.map(user, UserDto.class)).thenReturn(expectedDto);

        mockMvc.perform(get("/api/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@mail.com"));
    }
}