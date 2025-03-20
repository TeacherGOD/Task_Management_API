package com.example.taskmanagement.util;

import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.entity.enums.UserRole;

public class SecurityUtils {
    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isAdmin(User user) {
        return user.getRole() == UserRole.ROLE_ADMIN;
    }

    public static boolean isNotAdmin(User user) {
        return !isAdmin(user);
    }
}
