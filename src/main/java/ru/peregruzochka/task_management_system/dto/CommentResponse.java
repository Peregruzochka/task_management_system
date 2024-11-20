package ru.peregruzochka.task_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

    @Schema(description = "Unique identifier of the comment.", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Text content of the comment.", example = "This is a sample comment.")
    private String text;

    @Schema(description = "Unique identifier of the author of the comment.", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID authorId;

    @Schema(description = "Username of the comment's author.", example = "john_doe")
    private String authorUsername;

    @Schema(description = "Unique identifier of the task that the comment is associated with.", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID taskId;

    @Schema(description = "The date and time when the comment was created.", example = "2024-11-20T14:30:00")
    private LocalDateTime createdAt;
}
