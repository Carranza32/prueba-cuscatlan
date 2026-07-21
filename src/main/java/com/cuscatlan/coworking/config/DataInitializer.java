package com.cuscatlan.coworking.config;

import com.cuscatlan.coworking.domain.Role;
import com.cuscatlan.coworking.domain.User;
import com.cuscatlan.coworking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@test.com")) {
            User adminUser = User.builder()
                    .fullName("Admin User")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(adminUser);
        }
    }
}