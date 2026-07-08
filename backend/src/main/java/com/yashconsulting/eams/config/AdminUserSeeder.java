package com.yashconsulting.eams.config;

import com.yashconsulting.eams.security.Role;
import com.yashconsulting.eams.user.entity.User;
import com.yashconsulting.eams.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Checking for existence of an ADMIN user...");
        if (!userRepository.existsByRole(Role.ADMIN)) {
            log.info("No ADMIN user found in the database.");
            
            // Check if the default email is already taken to prevent Unique Constraint violation
            String defaultAdminEmail = "admin@eams.com";
            if (userRepository.existsByEmail(defaultAdminEmail)) {
                log.warn("Cannot create default ADMIN user: Email '{}' is already taken by a non-ADMIN user.", defaultAdminEmail);
                return;
            }

            log.info("Creating default ADMIN user: {}", defaultAdminEmail);
            User admin = User.builder()
                    .firstName("Admin")
                    .lastName("System")
                    .email(defaultAdminEmail)
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);
            log.info("Default ADMIN user successfully seeded.");
        } else {
            log.info("ADMIN user already exists. Seeding skipped.");
        }
    }
}
