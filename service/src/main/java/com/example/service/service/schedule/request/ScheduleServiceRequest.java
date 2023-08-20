package com.example.service.service.schedule.request;

import com.example.common.enums.IsUse;
import com.example.common.enums.SchedulePeriod;
import com.example.common.enums.ScheduleType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleServiceRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class UpdateTodaySchedule {

        private List<Element> clearScheduleTodoList;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class Element {

        private Long scheduleId;
        private List<Long> clearTodoIdList;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class CreateSchedule {

        private Long scheduleId;
        private String name;
        private Long studyId;
        private LocalDate startDate;
        private LocalDate endDate;
        private IsUse isUse;
        private ScheduleType scheduleType;
        private SchedulePeriod period;
        private Long customDay;
        private List<Long> todoList;
    }
}
