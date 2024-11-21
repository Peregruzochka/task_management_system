package ru.peregruzochka.task_management_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.UserRepository;

import static ru.peregruzochka.task_management_system.entity.UserRole.ROLE_ADMIN;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Transactional
    public void createAdminIfNotExists() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .username("admin")
                    .encodedPassword(passwordEncoder.encode(adminPassword))
                    .email(adminEmail)
                    .role(ROLE_ADMIN)
                    .build();

            userRepository.save(admin);
        }
    }
}
