package com.example.common.model.dto.schedule;

import com.example.common.utils.DateUtils;
import com.example.common.entity.schedule.Schedule;
import com.example.common.entity.schedule.ScheduleTodo;
import com.example.common.entity.study.Study;
import com.example.common.enums.IsClear;
import com.example.common.enums.IsUse;
import com.example.common.enums.SchedulePeriod;
import com.example.common.enums.ScheduleType;
import com.example.common.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder(access = AccessLevel.PRIVATE)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Getter
@ToString
public class ScheduleDto {

    private Long id;
    private Long memberId;
    private Long studyId;
    private String studyName;
    private String name;
    private List<TodoDto> todoList;
    private String startDate;
    private String endDate;
    private IsUse isUse;
    private String createdAt;
    private String updatedAt;
    private Double successRate;
    private SchedulePeriod period;
    private ScheduleType scheduleType;
    private String nextScheduleDate;
    private Long custom;

    public static ScheduleDto entityToDtoWithTodo(Schedule schedule) {
        return builder()
                .id(schedule.getId())
                .name(schedule.getName())
                .studyId(schedule.getStudy().getId())
                .memberId(getMemberId(schedule))
                .todoList(getTodoList(schedule))
                .nextScheduleDate(DateUtils.localDateToString(schedule.getNextScheduleDate()))
                .startDate(DateUtils.localDateToString(schedule.getStartDate()))
                .endDate(DateUtils.localDateToString(schedule.getEndDate()))
                .isUse(schedule.getIsUse())
                .period(schedule.getPeriod())
                .custom(schedule.getCustom())
                .scheduleType(schedule.getScheduleType())
                .createdAt(DateUtils.localDateTimeToString(schedule.getCreatedAt()))
                .updatedAt(DateUtils.localDateTimeToString(schedule.getUpdatedAt()))
                .build();
    }

    public static ScheduleDto entityToDto(Schedule schedule) {
        return builder()
                .id(schedule.getId())
                .name(schedule.getName())
                .memberId(getMemberId(schedule))
                .nextScheduleDate(DateUtils.localDateToString(schedule.getNextScheduleDate()))
                .startDate(DateUtils.localDateToString(schedule.getStartDate()))
                .endDate(DateUtils.localDateToString(schedule.getEndDate()))
                .isUse(schedule.getIsUse())
                .period(schedule.getPeriod())
                .custom(schedule.getCustom())
                .studyId(schedule.getStudy().getId())
                .studyName(schedule.getStudy().getName())
                .scheduleType(schedule.getScheduleType())
                .createdAt(DateUtils.localDateTimeToString(schedule.getCreatedAt()))
                .updatedAt(DateUtils.localDateTimeToString(schedule.getUpdatedAt()))
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

    public void updateTodoList(List<ScheduleTodo> scheduleTodoList) {
        this.successRate = (scheduleTodoList.stream()
                .filter(scheduleTodo -> scheduleTodo.getIsClear() == IsClear.Y)
                .count() / (double)scheduleTodoList.size()) * 100;
        this.todoList = scheduleTodoList.stream()
                .map(TodoDto::entityToDto)
                .collect(Collectors.toList());
    }
}
