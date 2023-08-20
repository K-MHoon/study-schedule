package com.example.service.service.schedule.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

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


}
