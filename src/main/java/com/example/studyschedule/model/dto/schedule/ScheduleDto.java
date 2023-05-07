package com.example.studyschedule.model.dto.schedule;

import com.example.studyschedule.entity.schedule.Schedule;
import com.example.studyschedule.entity.schedule.ScheduleTodo;
import com.example.studyschedule.entity.schedule.Todo;
import com.example.studyschedule.entity.study.Study;
import com.example.studyschedule.enums.IsClear;
import com.example.studyschedule.enums.IsUse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

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

    public static ScheduleDto entityToDtoWithTodo(Schedule schedule) {
        return ScheduleDto.builder()
                .id(schedule.getId())
                .name(schedule.getName())
                .studyId(schedule.getStudy().getId())
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

    public void updateStudy(Study study) {
        this.studyId = study.getId();
        this.studyName = study.getName();
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
