package ru.peregruzochka.task_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.entity.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);
}
