package ru.peregruzochka.task_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.dto.TaskFilterDto;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.TaskPriority;
import ru.peregruzochka.task_management_system.entity.TaskStatus;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.TaskRepository;
import ru.peregruzochka.task_management_system.repository.UserRepository;
import ru.peregruzochka.task_management_system.specification.TaskSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        User author = getUserById(task.getAuthor().getId());
        if (Objects.isNull(task.getAssignee().getId())) {
            task.setAssignee(author);
        }

        User assignee = getUserById(task.getAssignee().getId());

        defaultIfNull(task::setAssignee, assignee, author);
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
    public Task changePriority(UUID taskId, TaskPriority priority, UUID updaterId) {
        Task taskToUpdate = getTaskById(taskId);
        User updater = getUserById(updaterId);

        validateAdminUpdater(taskToUpdate, updater);

        TaskStatus oldPriority = taskToUpdate.getStatus();
        taskToUpdate.setPriority(priority);
        Task updatedTask = taskRepository.save(taskToUpdate);
        log.info("Task updated priority {} -> {}: {}", oldPriority, priority, updatedTask);
        return updatedTask;
    }

    @Transactional
    public Task changeAssignee(UUID taskId, UUID assigneeId, UUID updaterId) {
        Task taskToUpdate = getTaskById(taskId);
        User updater = getUserById(updaterId);

        validateAdminUpdater(taskToUpdate, updater);

        User newAssignee = getUserById(assigneeId);
        String oldAssigneeEmail = taskToUpdate.getAssignee().getEmail();
        taskToUpdate.setAssignee(newAssignee);
        Task updatedTask = taskRepository.save(taskToUpdate);
        log.info("Task updated assignee {} -> {}: {}", oldAssigneeEmail, newAssignee.getEmail(), updatedTask);
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

    @Transactional(readOnly = true)
    public List<Task> getTasks(int page, int size, TaskFilterDto filter) {
        Pageable pageable = PageRequest.of(page, size);

        if (Objects.isNull(filter)) {
            return taskRepository.findAll(pageable).getContent();
        }

        List<User> assignees = new ArrayList<>();
        if (Objects.nonNull(filter.getAssignees())) {
            assignees.addAll(userRepository.findAllById(filter.getAssignees()));
        }

        List<User> authors = new ArrayList<>();
        if (Objects.nonNull(filter.getAuthors())) {
            authors.addAll(userRepository.findAllById(filter.getAuthors()));
        }

        Specification<Task> spec = Specification.where(TaskSpecification.likeTitle(filter.getTitlePattern()))
                .and(TaskSpecification.hasAssignee(assignees))
                .and(TaskSpecification.hasAuthor(authors))
                .and(TaskSpecification.hasStatus(filter.getStatuses()))
                .and(TaskSpecification.hasPriority(filter.getPriorities()))
                .and(TaskSpecification.after(filter.getStartDate()))
                .and(TaskSpecification.before(filter.getEndDate()));

        return taskRepository.findAll(spec, pageable).getContent();
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
}
