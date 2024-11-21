package ru.peregruzochka.task_management_system.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.peregruzochka.task_management_system.entity.Comment;
import ru.peregruzochka.task_management_system.entity.Task;
import ru.peregruzochka.task_management_system.entity.User;
import ru.peregruzochka.task_management_system.repository.CommentRepository;
import ru.peregruzochka.task_management_system.repository.TaskRepository;
import ru.peregruzochka.task_management_system.repository.UserRepository;

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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_SuccessfullyCreatesComment_WhenUserAndTaskExist() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String text = "This is a test comment";

        User user = User.builder().id(userId).email("user@test.com").build();
        Task task = Task.builder().id(taskId).title("Test Task").build();
        Comment comment = Comment.builder().task(task).text(text).author(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment createdComment = commentService.createComment(userId, taskId, text);

        assertNotNull(createdComment);
        assertEquals(text, createdComment.getText());
        assertEquals(user, createdComment.getAuthor());
        assertEquals(task, createdComment.getTask());

        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, times(1)).findById(taskId);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_ThrowsException_WhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String text = "This is a test comment";

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> commentService.createComment(userId, taskId, text));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_ThrowsException_WhenTaskDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        String text = "This is a test comment";

        User user = User.builder().id(userId).email("user@test.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            commentService.createComment(userId, taskId, text));

        assertEquals("Task not found", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(taskRepository, times(1)).findById(taskId);
        verify(commentRepository, never()).save(any());
    }
}

