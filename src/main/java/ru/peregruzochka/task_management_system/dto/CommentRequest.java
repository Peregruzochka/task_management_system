package ru.peregruzochka.task_management_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request model for creating a comment associated with a specific task.")
public class CommentRequest {

    @NotNull(message = "TaskId cannot be empty")
    @Schema(description = "Unique identifier of the task the comment is associated with.", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID taskId;

    @NotNull(message = "Text cannot be empty")
    @Schema(description = "The content of the comment.", example = "This is a sample comment.")
    private String text;
}
