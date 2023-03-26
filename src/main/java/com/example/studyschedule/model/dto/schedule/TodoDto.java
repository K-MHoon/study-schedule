package com.example.studyschedule.model.dto.schedule;

import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.enums.IsClear;
import com.example.studyschedule.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

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

    public static TodoDto entityToDto(Todo todo) {
        return TodoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .content(todo.getContent())
                .createdAt(DateUtils.localDateTimeToString(todo.getCreatedAt()))
                .updatedAt(DateUtils.localDateTimeToString(todo.getUpdatedAt()))
                .build();
    }
}
