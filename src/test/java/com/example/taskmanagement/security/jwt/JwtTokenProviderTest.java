package com.example.taskmanagement.security.jwt;

import com.example.taskmanagement.entity.enums.UserRole;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import com.example.taskmanagement.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private final JwtTokenProvider jwtTokenProvider = new JwtTokenProvider();

    private final User testUser = User.builder()
            .email("test@mail.com")
            .password("password")
            .role(UserRole.ROLE_USER)
            .build();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", "secret-key");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 86400000);
    }

    @Test
    void generatedToken_ShouldContainValidEmailAndRoles() {

        UserDetailsImpl userDetails = new UserDetailsImpl(testUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(authentication);

        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("test@mail.com", jwtTokenProvider.getEmailFromToken(token));
    }
}