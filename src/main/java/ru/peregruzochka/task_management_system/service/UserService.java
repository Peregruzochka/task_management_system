package ru.peregruzochka.task_management_system.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;

    @PostConstruct
    public void init() {
        adminService.createSuperAdminIfNotExists();
    }

    @Transactional
    public User signUp(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email address already in use");
        }

        User savedUser = userRepository.save(user);
        log.info("Register new user: {}", savedUser.getEmail());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User signIn(User user) {
        String email = user.getEmail();
        User savedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email '" + email + "' is not registered"));

        validatePassword(user, savedUser);
        log.info("User with email '{}' logged in", savedUser.getEmail());
        return savedUser;
    }

    private void validatePassword(User user, User savedUser) {
        String currentPassword = savedUser.getEncodedPassword();
        String userPassword = user.getEncodedPassword();

        if (!passwordEncoder.matches(userPassword, currentPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
    }
}
