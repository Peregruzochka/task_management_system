package ru.peregruzochka.task_management_system.dto;

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
public class CommentResponseDto {
    private UUID id;
    private String text;
    private UUID authorId;
    private String authorUsername;
    private UUID taskId;
    private LocalDateTime createdAt;
}
