package ru.peregruzochka.task_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.peregruzochka.task_management_system.entity.TaskPriority;
import ru.peregruzochka.task_management_system.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for filtering tasks based on various criteria")
public class TaskFilterDto {

    @Schema(description = "Pattern for matching task titles", example = "bug fix")
    private String titlePattern;

    @Schema(description = "Start date for filtering tasks", example = "2024-11-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "End date for filtering tasks", example = "2024-11-30T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "List of task statuses to filter by", example = "['OPEN', 'IN_PROGRESS']")
    private List<TaskStatus> statuses;

    @Schema(description = "List of task priorities to filter by", example = "['LOW', 'HIGH']")
    private List<TaskPriority> priorities;

    @Schema(description = "List of assignee IDs to filter by", example = "['123e4567-e89b-12d3-a456-426614174002']")
    private List<UUID> assignees;

    @Schema(description = "List of author IDs to filter by", example = "['123e4567-e89b-12d3-a456-426614174001']")
    private List<UUID> authors;
}
