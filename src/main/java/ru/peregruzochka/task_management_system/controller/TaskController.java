package ru.peregruzochka.task_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.task_management_system.dto.TaskDto;
import ru.peregruzochka.task_management_system.dto.TaskFilterDto;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.TaskPriority;
import ru.peregruzochka.task_management_system.entity.TaskStatus;
import ru.peregruzochka.task_management_system.mapper.TaskMapper;
import ru.peregruzochka.task_management_system.service.TaskService;
import ru.peregruzochka.task_management_system.util.JwtTokenProvider;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Operations related to Task management")
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Create new task [Available to admin]", description = "Creates a new task and returns the created task")
    @SecurityRequirement(name = "Bearer Authentication")
    public TaskDto createTask(@RequestBody @Valid TaskDto taskDto,
                              @RequestHeader("Authorization") String authHeader) {
        UUID authorId = extractUserIdFromToken(authHeader);
        taskDto.setAuthorId(authorId);

        Task task = taskMapper.toTaskEntity(taskDto);
        Task createdTask = taskService.createTask(task);
        return taskMapper.toTaskDto(createdTask);
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Update/Edit an existing task [Available to admin]", description = "Updates an existing task and returns the updated task")
    @SecurityRequirement(name = "Bearer Authentication")
    public TaskDto updateTask(@RequestBody @Valid TaskDto taskDto,
                              @RequestHeader("Authorization") String authHeader) {
        UUID updaterId = extractUserIdFromToken(authHeader);

        Task task = taskMapper.toTaskEntity(taskDto);
        Task updatedTask = taskService.updateTask(task, updaterId);
        return taskMapper.toTaskDto(updatedTask);
    }

    @PutMapping("/{taskId}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Change task status [Available to all users after authorization]", description = "Updates the status of a task by its ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public TaskDto changeStatus(@PathVariable UUID taskId,
                                @RequestParam("value") TaskStatus status,
                                @RequestHeader("Authorization") String authHeader) {
        UUID updaterId = extractUserIdFromToken(authHeader);

        Task updatedTask = taskService.changeStatus(taskId, status, updaterId);
        return taskMapper.toTaskDto(updatedTask);
    }

    @PutMapping("/{taskId}/priority")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Change task priority [Available to admin]", description = "Updates the priority of a task by its ID")
    @SecurityRequirement(name = "Bearer Authentication")
    public TaskDto changePriority(@PathVariable UUID taskId,
                                  @RequestParam("value") TaskPriority priority,
                                  @RequestHeader("Authorization") String authHeader) {
        UUID updaterId = extractUserIdFromToken(authHeader);

        Task updatedTask = taskService.changePriority(taskId, priority, updaterId);
        return taskMapper.toTaskDto(updatedTask);
    }

    @PutMapping("/{taskId}/assignee")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Assign task to a user [Available to admin]", description = "Assigns a user as the assignee of a task")
    @SecurityRequirement(name = "Bearer Authentication")
    public TaskDto changeAssignee(@PathVariable UUID taskId,
                                  @RequestParam("id") UUID assigneeId,
                                  @RequestHeader("Authorization") String authHeader) {
        UUID updaterId = extractUserIdFromToken(authHeader);

        Task updatedTask = taskService.changeAssignee(taskId, assigneeId, updaterId);
        return taskMapper.toTaskDto(updatedTask);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Delete a task [Available to admin]", description = "Deletes a task by its ID and returns the deleted task ")
    @SecurityRequirement(name = "Bearer Authentication")
    public TaskDto deleteTask(@PathVariable UUID taskId,
                              @RequestHeader("Authorization") String authHeader) {
        UUID deleterId = extractUserIdFromToken(authHeader);

        Task deletedTask = taskService.deleteTask(taskId, deleterId);
        return taskMapper.toTaskDto(deletedTask);
    }

    @PostMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Search tasks [Available to all users after authorization]", description = "Searches and filters tasks with optional pagination. Filtering is done based on all parameters that are added to the request body")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<TaskDto> getTasks(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestBody TaskFilterDto filter) {

        List<Task> tasks = taskService.getTasks(page, size, filter);
        return taskMapper.toTaskDtoPage(tasks);
    }

    private UUID extractUserIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtTokenProvider.extractUserId(token);
    }
}
