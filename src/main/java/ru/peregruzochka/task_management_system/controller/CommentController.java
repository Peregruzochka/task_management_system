package ru.peregruzochka.task_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.peregruzochka.task_management_system.dto.CommentRequest;
import ru.peregruzochka.task_management_system.dto.CommentResponse;
import ru.peregruzochka.task_management_system.entity.Comment;
import ru.peregruzochka.task_management_system.mapper.CommentMapper;
import ru.peregruzochka.task_management_system.service.CommentService;
import ru.peregruzochka.task_management_system.util.JwtTokenProvider;

import java.util.UUID;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(
        name = "Comments",
        description = """
                This controller is responsible for handling comment creation and retrieval.
                It allows users to create comments related to tasks, and each comment is associated with a task.
                The actions require user authentication via a Bearer token.
                """
)
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a comment. [Available to all users after authorization]",
            description = "Creates a new comment associated with a task. Requires authorization with a Bearer token."
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public CommentResponse createComment(@RequestBody CommentRequest commentRequest,
                                         @RequestHeader("Authorization") String authHeader) {
        UUID userId = extractUserIdFromToken(authHeader);

        Comment comment = commentService.createComment(userId, commentRequest.getTaskId(), commentRequest.getText());
        return commentMapper.toCommentDto(comment);
    }

    private UUID extractUserIdFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtTokenProvider.extractUserId(token);
    }
}
