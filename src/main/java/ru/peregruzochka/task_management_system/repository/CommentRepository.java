package ru.peregruzochka.task_management_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.peregruzochka.task_management_system.entity.Comment;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
}
