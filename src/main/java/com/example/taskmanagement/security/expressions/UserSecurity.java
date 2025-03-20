package com.example.taskmanagement.security.expressions;

import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.services.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {
    private final UserRepository userRepository;

    public boolean isSelf(Long userId, UserDetailsImpl userDetails) {
        return userDetails.getId().equals(userId);
    }
}
