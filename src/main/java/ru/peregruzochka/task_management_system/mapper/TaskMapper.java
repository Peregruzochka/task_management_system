package ru.peregruzochka.task_management_system.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.peregruzochka.task_management_system.dto.TaskDto;
import ru.peregruzochka.task_management_system.entity.Comment;
import ru.peregruzochka.task_management_system.entity.Task;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "assignee.id", source = "assigneeId")
    Task toTaskEntity(TaskDto taskDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    @Mapping(target = "commentsIds", source = "comments", qualifiedByName = "commentsToIds")
    TaskDto toTaskDto(Task task);

    List<TaskDto> toTaskDtoPage(List<Task> tasks);

    @Named("commentsToIds")
    default List<UUID> mapCommentsToIds(List<Comment> comments) {
        if (Objects.isNull(comments)) {
           return List.of();
        }
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }
}
