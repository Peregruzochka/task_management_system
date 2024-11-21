package ru.peregruzochka.task_management_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.UserRepository;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.peregruzochka.task_management_system.entity.UserRole.ADMIN;
import static ru.peregruzochka.task_management_system.entity.UserRole.SUPER_ADMIN;
import static ru.peregruzochka.task_management_system.entity.UserRole.USER;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() throws Exception {

        setFieldValue(adminService, "superAdminEmail", "superadmin@test.com");
        setFieldValue(adminService, "superAdminPassword", "password");
        setFieldValue(adminService, "superAdminUsername", "superadmin");
    }

    private void setFieldValue(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void createSuperAdminIfNotExists_CreatesSuperAdmin_WhenNotExists() {
        when(userRepository.findByRole(SUPER_ADMIN)).thenReturn(List.of());
        when(userRepository.existsByEmail("superadmin@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        adminService.createSuperAdminIfNotExists();

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createSuperAdminIfNotExists_DoesNothing_WhenSuperAdminExists() {
        when(userRepository.findByRole(SUPER_ADMIN)).thenReturn(List.of(new User()));

        adminService.createSuperAdminIfNotExists();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createAdmin_SuccessfullyCreatesAdmin_WhenEmailNotExists() {
        User admin = User.builder().email("admin@test.com").username("admin").build();

        when(userRepository.existsByEmail("admin@test.com")).thenReturn(false);
        when(userRepository.save(admin)).thenReturn(admin);

        User createdAdmin = adminService.createAdmin(admin);

        assertEquals(ADMIN, createdAdmin.getRole());
        verify(userRepository, times(1)).save(admin);
    }

    @Test
    void createAdmin_ThrowsException_WhenEmailAlreadyExists() {
        User admin = User.builder().email("admin@test.com").build();

        when(userRepository.existsByEmail("admin@test.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminService.createAdmin(admin));

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void promoteToAdmin_PromotesUserToAdmin_WhenUserExists() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).role(USER).email("user@test.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User promotedUser = adminService.promoteToAdmin(userId);

        assertEquals(ADMIN, promotedUser.getRole());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void promoteToAdmin_ThrowsException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminService.promoteToAdmin(userId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void demoteFromAdmin_DemotesAdminToUser_WhenAdminExists() {
        UUID userId = UUID.randomUUID();
        User admin = User.builder().id(userId).role(ADMIN).email("admin@test.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(admin));
        when(userRepository.save(admin)).thenReturn(admin);

        User demotedUser = adminService.demoteFromAdmin(userId);

        assertEquals(USER, demotedUser.getRole());
        verify(userRepository, times(1)).save(admin);
    }

    @Test
    void demoteFromAdmin_ThrowsException_WhenUserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> adminService.demoteFromAdmin(userId));

        assertEquals("User not found", exception.getMessage());
    }
}
