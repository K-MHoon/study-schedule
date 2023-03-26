package com.example.studyschedule.model.dto.schedule;

import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.ScheduleTodo;
import com.example.studyschedule.enums.IsUse;
import com.example.studyschedule.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.studyschedule.utils.DateUtils.localDateTimeToString;

@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Getter
@ToString
public class ScheduleDto {

    private Long id;
    private Long memberId;
    private String name;
    private List<TodoDto> todoList;
    private String startDate;
    private String endDate;
    private IsUse isUse;
    private String createdAt;
    private String updatedAt;

    public static ScheduleDto entityToDtoWithTodo(Schedule schedule) {
        return ScheduleDto.builder()
                .id(schedule.getId())
                .name(schedule.getName())
                .memberId(getMemberId(schedule))
                .todoList(getTodoList(schedule))
                .startDate(localDateTimeToString(schedule.getStartDate()))
                .endDate(localDateTimeToString(schedule.getEndDate()))
                .isUse(schedule.getIsUse())
                .createdAt(localDateTimeToString(schedule.getCreatedAt()))
                .updatedAt(localDateTimeToString(schedule.getUpdatedAt()))
                .build();
    }

    public static ScheduleDto entityToDto(Schedule schedule) {
        return ScheduleDto.builder()
                .id(schedule.getId())
                .name(schedule.getName())
                .memberId(getMemberId(schedule))
                .startDate(localDateTimeToString(schedule.getStartDate()))
                .endDate(localDateTimeToString(schedule.getEndDate()))
                .isUse(schedule.getIsUse())
                .createdAt(localDateTimeToString(schedule.getCreatedAt()))
                .updatedAt(localDateTimeToString(schedule.getUpdatedAt()))
                .build();
    }

    private static Long getMemberId(Schedule schedule) {
        if(Objects.isNull(schedule.getMember())) return null;
        return schedule.getMember().getId();
    }

    private static List<TodoDto> getTodoList(Schedule schedule) {
        assert Objects.nonNull(schedule.getScheduleTodoList());

        return schedule.getScheduleTodoList().stream()
                .map(ScheduleTodo::getTodo)
                .map(TodoDto::entityToDto)
                .collect(Collectors.toList());
    }
}
