package com.lms.lms.config;

import com.lms.lms.entity.Role;
import com.lms.lms.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataSeeder — runs once when the application starts.
 *
 * WHY do we need this?
 * - When the app starts for the first time, the database is empty.
 * - We need at least one ADMIN user to log in and create other users.
 * - This seeder creates a default admin account if one doesn't exist.
 *
 * CommandLineRunner: Spring calls the run() method after the app context is
 * ready.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;

    public DataSeeder(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only create the default admin if no user with that username exists
        if (!userService.usernameExists("admin")) {
            userService.createUser(
                    "admin", // username
                    "admin123", // password (will be BCrypt hashed by UserService)
                    "System Admin", // full name
                    Role.ADMIN // role
            );
            System.out.println("✅ Default admin user created (username: admin, password: admin123)");
        } else {
            System.out.println("ℹ️  Admin user already exists, skipping seed.");
        }
    }
}
