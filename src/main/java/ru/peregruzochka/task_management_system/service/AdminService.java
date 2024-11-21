package ru.peregruzochka.task_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.UserRepository;

import java.util.UUID;

import static ru.peregruzochka.task_management_system.entity.UserRole.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${super-admin.email}")
    private String superAdminEmail;

    @Value("${super-admin.password}")
    private String superAdminPassword;

    @Value("${super-admin.username}")
    private String superAdminUsername;

    @Transactional
    public void createSuperAdminIfNotExists() {
        if (!userRepository.existsByEmail(superAdminEmail)) {
            User admin = User.builder()
                    .username(superAdminUsername)
                    .encodedPassword(passwordEncoder.encode(superAdminPassword))
                    .email(superAdminEmail)
                    .role(SUPER_ADMIN)
                    .build();

            userRepository.save(admin);
            log.info("Super admin created");
        }
    }

    @Transactional
    public User createAdmin(User admin) {
        if (userRepository.existsByEmail(admin.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        admin.setRole(ADMIN);
        User savedAdmin = userRepository.save(admin);
        log.info("Admin created {}", savedAdmin.getEmail());
        return savedAdmin;
    }

    @Transactional
    public User promoteToAdmin(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found"));

        user.setRole(ADMIN);
        User newAdmin = userRepository.save(user);
        log.info("Make admin {} -> {}: {}", user.getRole(), newAdmin.getRole(), newAdmin.getEmail());
        return newAdmin;
    }

    @Transactional
    public User demoteFromAdmin(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User not found"));

        user.setRole(USER);
        User demoteUser = userRepository.save(user);
        log.info("Demote {} -> {}: {}", user.getRole(), demoteUser.getRole(), demoteUser.getEmail());
        return demoteUser;
    }
}
