package com.example.common.model.request.schedule;

import com.example.common.enums.IsUse;
import com.example.common.enums.SchedulePeriod;
import com.example.common.enums.ScheduleType;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleControllerRequest {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTodayScheduleRequest {

        @NotNull
        @Valid
        private List<Element> clearScheduleTodoList;
    }


    @Getter
    public static class Element {

        @NotNull
        private Long scheduleId;

        @NotNull
        private List<Long> clearTodoIdList;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    public static class CreateScheduleRequest {

        @Nullable
        private Long scheduleId;

        @NotBlank
        private String name;

        @NotNull
        private Long studyId;

        @NotNull
        private LocalDateTime startDate;

        @NotNull
        private LocalDateTime endDate;

        @NotNull
        private IsUse isUse;

        @NotNull
        private ScheduleType scheduleType;

        @NotNull
        private SchedulePeriod period;

        @Nullable
        private Long customDay;

        @NotNull
        @UniqueElements
        private List<Long> todoList;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteScheduleRequest {

        @UniqueElements
        @NotNull
        private List<Long> scheduleList;
    }
}