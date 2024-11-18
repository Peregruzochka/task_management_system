package ru.peregruzochka.task_management_system.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.peregruzochka.task_management_system.dto.TaskDto;
import ru.peregruzochka.task_management_system.entity.Task;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "author.id", source = "authorId")
    @Mapping(target = "assignee.id", source = "assigneeId")
    Task toTaskEntity(TaskDto taskDto);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    TaskDto toTaskDto(Task task);
}
