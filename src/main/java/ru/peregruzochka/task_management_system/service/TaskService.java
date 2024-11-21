package ru.peregruzochka.task_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.TaskStatus;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.TaskRepository;
import ru.peregruzochka.task_management_system.repository.UserRepository;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

import static ru.peregruzochka.task_management_system.entity.TaskPriority.LOW;
import static ru.peregruzochka.task_management_system.entity.TaskStatus.TODO;
import static ru.peregruzochka.task_management_system.entity.UserRole.ADMIN;
import static ru.peregruzochka.task_management_system.entity.UserRole.USER;

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
        if (Objects.nonNull(task.getAssignee())) {
            validateUser(task.getAssignee(), "assignee");
        }

        defaultIfNull(task::setAssignee, task.getAssignee(), task.getAuthor());
        defaultIfNull(task::setPriority, task.getPriority(), LOW);
        defaultIfNull(task::setStatus, task.getStatus(), TODO);

        Task createdTask = taskRepository.save(task);
        log.info("Task created: {}", createdTask);
        return createdTask;
    }

    @Transactional
    public Task updateTask(Task task, UUID updaterId) {
        Task taskToUpdate = getTaskById(task.getId());
        User updater = getUserById(updaterId);

        validateAdminUpdater(taskToUpdate, updater);

        updateField(taskToUpdate::setTitle, task.getTitle());
        updateField(taskToUpdate::setDescription, task.getDescription());
        updateField(taskToUpdate::setPriority, task.getPriority());
        updateField(taskToUpdate::setStatus, task.getStatus());

        Task updatedTask = taskRepository.save(taskToUpdate);
        log.info("Task updated: {}", updatedTask);
        return updatedTask;
    }

    @Transactional
    public Task changeStatus(UUID taskId, TaskStatus status, UUID updaterId) {
        Task taskToUpdate = getTaskById(taskId);
        User updater = getUserById(updaterId);

        validateAdminUpdater(taskToUpdate, updater);
        validateUserUpdater(taskToUpdate, updater);

        TaskStatus oldStatus = taskToUpdate.getStatus();
        taskToUpdate.setStatus(status);
        Task updatedTask = taskRepository.save(taskToUpdate);
        log.info("Task updated status {} -> {}: {}", oldStatus, status, updatedTask);
        return updatedTask;
    }

    @Transactional
    public Task deleteTask(UUID taskId, UUID deleterId) {
        Task deletedTask = getTaskById(taskId);
        User deleter = getUserById(deleterId);

        validateAdminUpdater(deletedTask, deleter);

        taskRepository.deleteById(taskId);
        return deletedTask;
    }

    private Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User with id " + userId + " not found"));
    }

    private void validateAdminUpdater(Task taskToUpdate, User updater) {
        if (updater.getRole().equals(ADMIN) && !taskToUpdate.getAuthor().getId().equals(updater.getId())) {
            throw new IllegalStateException("No permission to update task");
        }
    }

    private void validateUserUpdater(Task taskToUpdate, User updater) {
        if (updater.getRole().equals(USER) && !taskToUpdate.getAssignee().getId().equals(updater.getId())) {
            throw new IllegalStateException("No permission to update task");
        }
    }

    private <T> void updateField(Consumer<T> setter, T value) {
        if (Objects.nonNull(value)) {
            setter.accept(value);
        }
    }

    private <T> void defaultIfNull(Consumer<T> setter, T value, T defaultValue) {
        if (Objects.isNull(value)) {
            setter.accept(defaultValue);
        }
    }

    private void validateUser(User user, String role) {
        if (!userRepository.existsById(user.getId())) {
            log.error("This user cannot be the {} because they do not exist. Invalid user id = {}", role, user.getId());
            throw new IllegalArgumentException(String.format("This user cannot be the %s because they do not exist", role));
        }
    }
}
