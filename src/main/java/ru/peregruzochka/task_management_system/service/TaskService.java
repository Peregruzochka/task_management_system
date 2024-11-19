package ru.peregruzochka.task_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.TaskStatus;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.UserRepository;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

    @Transactional
    public Task updateTask(Task task) {
        Task taskToUpdate = getTaskById(task.getId());
        validateAuthor(task, taskToUpdate);

        updateField(task::getTitle, taskToUpdate::setTitle);
        updateField(task::getDescription, taskToUpdate::setDescription);
        updateField(task::getPriority, taskToUpdate::setPriority);
        updateField(task::getStatus, taskToUpdate::setStatus);

        if (Objects.nonNull(task.getAssignee())) {
            validateAssigneeExists(task.getAssignee().getId());
            taskToUpdate.setAssignee(task.getAssignee());
        }

        Task updatedTask = taskRepository.save(taskToUpdate);
        log.info("Task updated: {}", updatedTask);
        return updatedTask;
    }

    @Transactional
    public Task changeStatus(UUID taskId, TaskStatus status) {
        Task taskToUpdate = getTaskById(taskId);
        TaskStatus oldStatus = taskToUpdate.getStatus();
        taskToUpdate.setStatus(status);
        Task updatedTask = taskRepository.save(taskToUpdate);
        log.info("Task updated status {} -> {}: {}", oldStatus, status, updatedTask);
        return updatedTask;
    }

    private Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    private void validateAuthor(Task task, Task taskToUpdate) {
        if (!task.getAuthor().getId().equals(taskToUpdate.getAuthor().getId())) {
            throw new IllegalArgumentException("Cannot change the task author");
        }
    }

    private void validateAssigneeExists(UUID assigneeId) {
        if (!userRepository.existsById(assigneeId)) {
            throw new IllegalArgumentException("New assignee not found");
        }
    }

    private <T> void updateField(Supplier<T> getter, Consumer<T> setter) {
        if (Objects.nonNull(getter.get())) {
            T value = getter.get();
            setter.accept(value);
        }
    }

    private void validateUser(User user, String role) {
        if (!userRepository.existsById(user.getId())) {
            log.error("This user cannot be the {} because they do not exist. Invalid user id = {}", role, user.getId());
            throw new IllegalArgumentException(String.format("This user cannot be the %s because they do not exist", role));
        }
    }


}
