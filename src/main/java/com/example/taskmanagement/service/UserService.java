package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.request.UserRegistrationDto;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.entity.enums.UserRole;
import com.example.taskmanagement.exception.EmailAlreadyExistsException;
import com.example.taskmanagement.exception.UserNotFoundException;
import com.example.taskmanagement.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .role(UserRole.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    public User getAuthenticatedUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }



    @PostConstruct
    public void initFirstAdmin() {
        if (userRepository.countAdmins() == 0) {
            Dotenv dotenv = Dotenv.load();
            String adminEmail = dotenv.get("INIT_ADMIN_EMAIL");
            String adminPassword = dotenv.get("INIT_ADMIN_PASSWORD");
            String adminName = dotenv.get("INIT_ADMIN_NAME");

            if (adminEmail != null && !adminEmail.isBlank()
                    && adminPassword != null && !adminPassword.isBlank()) {
                User admin = User.builder()
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .fullName(adminName)
                        .role(UserRole.ROLE_ADMIN)
                        .build();
                userRepository.save(admin);
                log.info("Создан первый администратор," +
                        " будьте аккуратны и удалите/измените его данные в реальном приложении. Email:"+adminEmail);
            }
        }
    }
}