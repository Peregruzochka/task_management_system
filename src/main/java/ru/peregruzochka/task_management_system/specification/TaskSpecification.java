package ru.peregruzochka.task_management_system.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.TaskPriority;
import ru.peregruzochka.task_management_system.entity.TaskStatus;
import ru.peregruzochka.task_management_system.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TaskSpecification {
    public static Specification<Task> likeTitle(String pattern) {
        if (Objects.nonNull(pattern) && !pattern.isBlank()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"), "%" + pattern + "%");
        }
        return null;
    }

    public static Specification<Task> before(LocalDateTime before) {
        if (Objects.nonNull(before)) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("created"), before));
        }
        return null;
    }

    public static Specification<Task> after(LocalDateTime after) {
        if (Objects.nonNull(after)) {
            return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("created"), after));
        }
        return null;
    }

    public static Specification<Task> hasAuthor(List<User> authors) {
        if (Objects.nonNull(authors) && !authors.isEmpty()) {
            return (root, query, criteriaBuilder) -> root.get("author").in(authors);
        }
        return null;
    }

    public static Specification<Task> hasAssignee(List<User> assignees) {
        if (Objects.nonNull(assignees) && !assignees.isEmpty()) {
            return (root, query, criteriaBuilder) -> root.get("assignee").in(assignees);
        }
        return null;
    }

    public static Specification<Task> hasStatus(List<TaskStatus> statuses) {
        if (Objects.nonNull(statuses) && !statuses.isEmpty()) {
            return (root, query, criteriaBuilder) -> root.get("status").in(statuses);
        }
        return null;
    }

    public static Specification<Task> hasPriority(List<TaskPriority> priorities) {
        if (Objects.nonNull(priorities) && !priorities.isEmpty()) {
            return (root, query, criteriaBuilder) -> root.get("priority").in(priorities);
        }
        return null;
    }
}
