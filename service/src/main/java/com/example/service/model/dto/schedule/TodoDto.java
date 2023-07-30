package com.example.service.model.dto.schedule;

import com.example.service.entity.schedule.ScheduleTodo;
import com.example.service.entity.schedule.Todo;
import com.example.service.enums.IsClear;
import com.example.service.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Getter
@ToString
public class TodoDto {

    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private IsClear isClear;
    private String reason;
    private String clearDate;

    public static TodoDto entityToDto(Todo todo) {
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .content(todo.getContent())
                .createdAt(DateUtils.localDateTimeToString(todo.getCreatedAt()))
                .updatedAt(DateUtils.localDateTimeToString(todo.getUpdatedAt()))
                .build();
    }

    public static TodoDto entityToDto(ScheduleTodo scheduleTodo) {
        Todo todo = scheduleTodo.getTodo();
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .content(todo.getContent())
                .createdAt(DateUtils.localDateTimeToString(todo.getCreatedAt()))
                .updatedAt(DateUtils.localDateTimeToString(todo.getUpdatedAt()))
                .isClear(scheduleTodo.getIsClear())
                .reason(scheduleTodo.getReason())
                .clearDate(getClearDate(scheduleTodo))
                .build();
    }

    private static String getClearDate(ScheduleTodo scheduleTodo) {
        return scheduleTodo.getIsClear() == IsClear.Y
                ? DateUtils.localDateTimeToString(scheduleTodo.getUpdatedAt())
                : null;
    }
}
