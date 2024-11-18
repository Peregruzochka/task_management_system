package ru.peregruzochka.task_management_system.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Setter
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public Task createTask(Task task) {
        validateAuthor(task);
        validateAssignee(task);

        Task createdTask = taskRepository.save(task);
        log.info("Task created: {}", createdTask);
        return createdTask;
    }

    private void validateAssignee(Task task) {
        User assignee = task.getAssignee();
        if (!userRepository.existsById(assignee.getId())) {
            log.error("This user cannot be the assignee because he does not exist. Invalid user id = {}", assignee.getId());
            throw new IllegalArgumentException("This user cannot be the assignee because he does not exist");
        }
    }

    private void validateAuthor(Task task) {
        User author = task.getAuthor();
        if (!userRepository.existsById(author.getId())) {
            log.error("This user cannot be the author because he does not exist. Invalid user id = {}", author.getId());
            throw new IllegalArgumentException("This user cannot be the author because he does not exist");
        }
    }
}
