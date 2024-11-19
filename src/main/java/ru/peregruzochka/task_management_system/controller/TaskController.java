package ru.peregruzochka.task_management_system.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class TaskController {
    private final TaskMapper taskMapper;
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@RequestBody @Valid TaskDto taskDto) {
        Task task = taskMapper.toTaskEntity(taskDto);
        Task createdTask = taskService.createTask(task);
        return taskMapper.toTaskDto(createdTask);
    }

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto updateTask(@RequestBody @Valid TaskDto taskDto) {
        Task task = taskMapper.toTaskEntity(taskDto);
        Task updatedTask = taskService.updateTask(task);
        return taskMapper.toTaskDto(updatedTask);
    }

    @PutMapping("/{task-id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto changeStatus(@PathVariable(value = "task-id") UUID taskId, @RequestParam TaskStatus status) {
        Task updatedTask = taskService.changeStatus(taskId, status);
        return taskMapper.toTaskDto(updatedTask);
    }
}