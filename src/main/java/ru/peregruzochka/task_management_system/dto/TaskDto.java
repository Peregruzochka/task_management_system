package ru.peregruzochka.task_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.peregruzochka.task_management_system.entity.TaskPriority;
import ru.peregruzochka.task_management_system.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private UUID id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    @NotNull(message = "A task cannot but have an author")
    private UUID authorId;

    @NotNull(message = "A task cannot but have an executor")
    private UUID assigneeId;

    private LocalDateTime createdAt;
}
