package ru.peregruzochka.task_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.UserRepository;

import java.util.Objects;

import static ru.peregruzochka.task_management_system.entity.TaskPriority.LOW;
import static ru.peregruzochka.task_management_system.entity.TaskStatus.TODO;

@Slf4j
@Service
@RequiredArgsConstructor
@Setter
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public Task createTask(Task task) {
        validateUser(task.getAuthor(), "author");
        validateUser(task.getAssignee(), "assignee");

        if (Objects.isNull(task.getPriority())) {
            task.setPriority(LOW);
        }

        if (Objects.isNull(task.getStatus())) {
            task.setStatus(TODO);
        }

        Task createdTask = taskRepository.save(task);
        log.info("Task created: {}", createdTask);
        return createdTask;
    }

    private void validateUser(User user, String role) {
        if (user == null || !userRepository.existsById(user.getId())) {
            log.error("This user cannot be the {} because they do not exist. Invalid user id = {}", role,
                    user != null ? user.getId() : "null");
            throw new IllegalArgumentException(String.format("This user cannot be the %s because they do not exist", role));
        }
    }
}
