package ru.peregruzochka.task_management_system.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import ru.peregruzochka.task_management_system.dto.TaskFilterDto;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.TaskPriority;
import ru.peregruzochka.task_management_system.entity.TaskStatus;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.entity.UserRole;
import ru.peregruzochka.task_management_system.repository.TaskRepository;
import ru.peregruzochka.task_management_system.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;


    @Test
    void createTask_AssignsAuthorAsDefaultAssignee_WhenAssigneeIsNotSpecified() {
        UUID authorId = UUID.randomUUID();

        User author = User.builder()
                .id(authorId)
                .email("author@example.com")
                .build();

        Task taskWithoutAssignee = Task.builder()
                .title("Task without assignee")
                .author(author)
                .assignee(new User())
                .priority(TaskPriority.HIGH)
                .status(TaskStatus.TODO)
                .build();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(taskRepository.save(any(Task.class))).thenReturn(taskWithoutAssignee);

        Task resultTask = taskService.createTask(taskWithoutAssignee);

        assertNotNull(resultTask);
        assertEquals(author, resultTask.getAssignee());
        assertEquals(TaskPriority.HIGH, resultTask.getPriority());
        assertEquals(TaskStatus.TODO, resultTask.getStatus());
        assertEquals("Task without assignee", resultTask.getTitle());

        verify(userRepository, times(1)).findById(authorId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_UsesProvidedAssignee_WhenAssigneeIsSpecified() {
        UUID authorId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        User author = User.builder()
                .id(authorId)
                .email("author@example.com")
                .build();

        User assignee = User.builder()
                .id(assigneeId)
                .email("assignee@example.com")
                .build();

        Task taskWithAssignee = Task.builder()
                .title("Task with assignee")
                .author(author)
                .assignee(assignee)
                .priority(TaskPriority.MEDIUM)
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(assignee));
        when(taskRepository.save(any(Task.class))).thenReturn(taskWithAssignee);

        Task resultTask = taskService.createTask(taskWithAssignee);

        assertNotNull(resultTask);
        assertEquals(assignee, resultTask.getAssignee());
        assertEquals(TaskPriority.MEDIUM, resultTask.getPriority());
        assertEquals(TaskStatus.IN_PROGRESS, resultTask.getStatus());
        assertEquals("Task with assignee", resultTask.getTitle());

        verify(userRepository, times(1)).findById(authorId);
        verify(userRepository, times(1)).findById(assigneeId);
        verify(taskRepository, times(1)).save(any(Task.class));
    }


    @Test
    void createTask_ThrowsException_WhenAuthorNotFound() {
        UUID userId = UUID.randomUUID();
        Task task = Task.builder().author(User.builder().id(userId).build()).build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.createTask(task));

        assertEquals("User with id " + userId + " not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTask_SuccessfullyUpdatesTask() {
        UUID taskId = UUID.randomUUID();
        UUID updaterId = UUID.randomUUID();

        User updater = User.builder()
                .id(updaterId)
                .role(UserRole.ADMIN)
                .build();

        Task oldTask = Task.builder()
                .id(taskId)
                .title("Old task")
                .author(updater)
                .build();

        Task newTask = Task.builder()
                .id(taskId)
                .title("New task")
                .author(updater)
                .assignee(new User())
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(oldTask));
        when(userRepository.findById(updaterId)).thenReturn(Optional.of(updater));
        when(taskRepository.save(oldTask)).thenReturn(newTask);

        Task task = taskService.updateTask(newTask, updaterId);

        assertNotNull(newTask);
        assertEquals("New task", task.getTitle());

        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, times(1)).findById(updaterId);
        verify(taskRepository, times(1)).save(oldTask);
    }

    @Test
    void updateTask_ThrowsException_WhenTaskNotFound() {
        UUID taskId = UUID.randomUUID();
        UUID updaterId = UUID.randomUUID();
        Task updatedTaskDetails = Task.builder().id(taskId).build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.updateTask(updatedTaskDetails, updaterId));

        assertEquals("Task not found", exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void changeStatus_SuccessfullyChangesTaskStatus() {
        UUID taskId = UUID.randomUUID();
        UUID updaterId = UUID.randomUUID();
        User updater = User.builder().id(updaterId).role(UserRole.ADMIN).build();
        Task taskToUpdate = Task.builder().id(taskId).author(updater).status(TaskStatus.TODO).build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskToUpdate));
        when(userRepository.findById(updaterId)).thenReturn(Optional.of(updater));
        when(taskRepository.save(any(Task.class))).thenReturn(taskToUpdate);

        Task updatedTask = taskService.changeStatus(taskId, TaskStatus.DONE, updaterId);

        assertNotNull(updatedTask);
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());

        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, times(1)).findById(updaterId);
        verify(taskRepository, times(1)).save(taskToUpdate);
    }

    @Test
    void deleteTask_SuccessfullyDeletesTask() {
        UUID taskId = UUID.randomUUID();
        UUID deleterId = UUID.randomUUID();
        User deleter = User.builder().id(deleterId).role(UserRole.ADMIN).build();
        Task taskToDelete = Task.builder().id(taskId).author(deleter).build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskToDelete));
        when(userRepository.findById(deleterId)).thenReturn(Optional.of(deleter));

        Task deletedTask = taskService.deleteTask(taskId, deleterId);

        assertNotNull(deletedTask);
        assertEquals(taskId, deletedTask.getId());

        verify(taskRepository, times(1)).findById(taskId);
        verify(userRepository, times(1)).findById(deleterId);
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Captor
    ArgumentCaptor<Specification<Task>> captor;

    @Test
    void getTasks_ReturnsFilteredTasks() {
        int page = 0;
        int size = 10;
        TaskFilterDto filter = new TaskFilterDto();
        List<Task> tasks = Collections.singletonList(Task.builder().title("Test Task").build());
        Page<Task> pageResult = new PageImpl<>(tasks);

        when(taskRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageResult);

        List<Task> resultTasks = taskService.getTasks(page, size, filter);

        assertNotNull(resultTasks);
        assertEquals(1, resultTasks.size());
        assertEquals("Test Task", resultTasks.get(0).getTitle());

        verify(taskRepository, times(1)).findAll(captor.capture(), any(Pageable.class));

        Specification<Task> capturedSpec = captor.getValue();
        assertNotNull(capturedSpec);
    }
}

