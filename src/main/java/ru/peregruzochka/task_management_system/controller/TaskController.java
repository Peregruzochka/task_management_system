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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.task_management_system.dto.TaskDto;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.TaskStatus;
import ru.peregruzochka.task_management_system.mapper.TaskMapper;
import ru.peregruzochka.task_management_system.service.TaskService;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Operations related to Task management")
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new task [Available to admin]", description = "Creates a new task and returns the created task")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskDto createTask(@RequestBody @Valid TaskDto taskDto) {
        Task task = taskMapper.toTaskEntity(taskDto);
        Task createdTask = taskService.createTask(task);
        return taskMapper.toTaskDto(createdTask);
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update/Edit an existing task [Available to admin]", description = "Updates an existing task and returns the updated task")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskDto updateTask(@RequestBody @Valid TaskDto taskDto) {
        Task task = taskMapper.toTaskEntity(taskDto);
        Task updatedTask = taskService.updateTask(task);
        return taskMapper.toTaskDto(updatedTask);
    }

    @PutMapping("/{task-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Change task status. [Available to all users after authorization]", description = "Updates the status of a task by its ID")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public TaskDto changeStatus(@PathVariable(value = "task-id") UUID taskId, @RequestParam TaskStatus status) {
        Task updatedTask = taskService.changeStatus(taskId, status);
        return taskMapper.toTaskDto(updatedTask);
    }

    @DeleteMapping("/{task-id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a task. [Available to admin]", description = "Deletes a task by its ID and returns the deleted task")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TaskDto deleteTask(@PathVariable(value = "task-id") UUID taskId) {
        Task deletedTask = taskService.deleteTask(taskId);
        return taskMapper.toTaskDto(deletedTask);
    }
}
