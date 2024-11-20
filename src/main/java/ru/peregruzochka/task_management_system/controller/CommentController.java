package ru.peregruzochka.task_management_system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.task_management_system.dto.CommentRequestDto;
import ru.peregruzochka.task_management_system.dto.CommentResponseDto;
import ru.peregruzochka.task_management_system.entity.Comment;
import ru.peregruzochka.task_management_system.mapper.CommentMapper;
import ru.peregruzochka.task_management_system.service.CommentService;
import ru.peregruzochka.task_management_system.util.JwtTokenProvider;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@RequestBody CommentRequestDto commentDto,
                                            @RequestHeader("Authorization") String authHeader) {

        UUID taskId = commentDto.getTaskId();
        String text = commentDto.getText();
        String token = authHeader.replace("Bearer ", "");
        UUID userId = jwtTokenProvider.extractUserId(token);
        Comment savedComment = commentService.createComment(userId, taskId, text);
        return commentMapper.toCommentDto(savedComment);
    }
}
