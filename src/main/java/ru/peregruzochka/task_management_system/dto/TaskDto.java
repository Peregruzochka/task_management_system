package ru.peregruzochka.task_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.peregruzochka.task_management_system.entity.TaskPriority;
import ru.peregruzochka.task_management_system.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for a task")
public class TaskDto {

    @Schema(description = "Unique identifier for the task", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @NotBlank(message = "Title cannot be empty")
    @Schema(description = "Title of the task", example = "Fix login bug")
    private String title;

    @Schema(description = "Detailed description of the task", example = "Investigate and resolve the issue with user authentication.")
    private String description;

    @Schema(description = "Current status of the task", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "Priority of the task", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "ID of the user who created the task", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID authorId;

    @Schema(description = "ID of the user assigned to execute the task", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID assigneeId;

    @Schema(description = "Timestamp when the task was created", example = "2023-11-20T10:15:30")
    private LocalDateTime createdAt;
}