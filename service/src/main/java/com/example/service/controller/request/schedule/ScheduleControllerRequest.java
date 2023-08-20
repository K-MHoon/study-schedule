package com.example.service.controller.request.schedule;

import com.example.common.enums.IsUse;
import com.example.common.enums.SchedulePeriod;
import com.example.common.enums.ScheduleType;
import com.example.service.service.schedule.request.ScheduleServiceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleControllerRequest {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTodaySchedule {

        @NotNull
        @Valid
        private List<Element> clearScheduleTodoList;

        public ScheduleServiceRequest.UpdateTodaySchedule toServiceRequest() {
            List<ScheduleServiceRequest.Element> serviceRequestElementList = this.clearScheduleTodoList.stream()
                    .map(Element::toServiceRequest)
                    .collect(Collectors.toList());

            return ScheduleServiceRequest.UpdateTodaySchedule.builder()
                    .clearScheduleTodoList(serviceRequestElementList)
                    .build();
        }
    }

    @Getter
    public static class Element {

        @NotNull
        private Long scheduleId;

        @NotNull
        @UniqueElements
        private List<Long> clearTodoIdList;

        public ScheduleServiceRequest.Element toServiceRequest() {
            return ScheduleServiceRequest.Element.builder()
                    .scheduleId(this.scheduleId)
                    .clearTodoIdList(this.clearTodoIdList)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    public static class CreateScheduleRequest {

        private Long scheduleId;

        @NotBlank
        private String name;

        @NotNull
        private Long studyId;

        @NotNull
        private LocalDate startDate;

        @NotNull
        private LocalDate endDate;

        @NotNull
        private IsUse isUse;

        @NotNull
        private ScheduleType scheduleType;

        @NotNull
        private SchedulePeriod period;

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
