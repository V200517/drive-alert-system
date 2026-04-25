package com.yashasvi.drivealert.service;

import com.yashasvi.drivealert.entity.User;
import com.yashasvi.drivealert.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminRegistrationKey;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       @Value("${app.admin.registration-key}") String adminRegistrationKey) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminRegistrationKey = adminRegistrationKey;
    }

    public User registerUser(User user) {
        return saveUserWithRole(user, "ROLE_USER");
    }

    public User registerAdminUser(User user, String accessKey) {
        if (!StringUtils.hasText(accessKey) || !adminRegistrationKey.equals(accessKey.trim())) {
            throw new IllegalArgumentException("Invalid admin access key");
        }
        return saveUserWithRole(user, "ROLE_ADMIN");
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private User saveUserWithRole(User user, String role) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        return userRepository.save(user);
    }
}
