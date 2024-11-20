package ru.peregruzochka.task_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.entity.Comment;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.CommentRepository;
import ru.peregruzochka.task_management_system.repository.TaskRepository;
import ru.peregruzochka.task_management_system.repository.UserRepository;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public Comment createComment(UUID userId, UUID taskId, String text) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User not found"));

        Task task = taskRepository.findById(taskId).orElseThrow(
                () -> new IllegalArgumentException("Task not found"));

        Comment comment = Comment.builder()
                .task(task)
                .text(text)
                .author(user)
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Create comment: {}, author: {}, task: {}<{}>", savedComment.getText(), user.getEmail(),
                task.getTitle(), taskId);
        return savedComment;
    }
}
