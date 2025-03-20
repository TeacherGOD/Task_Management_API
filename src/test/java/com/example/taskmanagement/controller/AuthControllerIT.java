package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.request.AuthRequestDto;
import com.example.taskmanagement.dto.request.UserRegistrationDto;
import com.example.taskmanagement.dto.response.AuthResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void signupAndSigninShouldReturnValidJwt() {
        UserRegistrationDto regDto = new UserRegistrationDto(
                "user@test.com",
                "password",
                "Test User"
        );

        ResponseEntity<Void> regResponse = restTemplate.postForEntity(
                "/api/auth/signup",
                regDto,
                Void.class
        );
        assertEquals(HttpStatus.CREATED, regResponse.getStatusCode());
        AuthRequestDto authDto = new AuthRequestDto("user@test.com", "password");
        ResponseEntity<AuthResponseDto> authResponse = restTemplate.postForEntity(
                "/api/auth/signin",
                authDto,
                AuthResponseDto.class
        );
        assertEquals(HttpStatus.OK, authResponse.getStatusCode());
        assertNotNull(authResponse.getBody().getAccessToken());
        assertTrue(authResponse.getBody().getAccessToken().startsWith("eyJ"));
    }

    @Test
    void signup_WithExistingEmail_ShouldReturnConflict() {
        UserRegistrationDto regDto = new UserRegistrationDto(
                "user1@test.com",
                "password",
                "Test User1"
        );
        restTemplate.postForEntity("/api/auth/signup", regDto, Void.class);
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "/api/auth/signup",
                regDto,
                Void.class
        );
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

}