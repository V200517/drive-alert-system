package com.yashasvi.drivealert.config;

import com.yashasvi.drivealert.entity.User;
import com.yashasvi.drivealert.repository.UserRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultAdmin() {
        if (!userRepository.existsByEmail("admin@drivealert.com")) {
            User admin = User.builder()
                    .name("Admin")
                    .email("admin@drivealert.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .course("Admin")
                    .cgpa(10.0)
                    .year(0)
                    .skills("admin")
                    .role("ROLE_ADMIN")
                    .build();
            userRepository.save(admin);
        }
    }
}
