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

        @NotNull(message = "{common.list.not-null}")
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

        @NotNull(message = "{schedule.id.not-null}")
        private Long scheduleId;

        @NotNull(message = "{common.list.not-null}")
        @UniqueElements(message = "{common.list.unique-elements}")
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
    public static class CreateSchedule {

        private Long scheduleId;

        @NotBlank(message = "{schedule.name.not-blank}")
        private String name;

        @NotNull(message = "{study.id.not-null}")
        private Long studyId;

        @NotNull(message = "{schedule.date.start.not-null}")
        private LocalDate startDate;

        @NotNull(message = "{schedule.date.end.not-null}")
        private LocalDate endDate;

        @NotNull(message = "{schedule.is-use.not-null}")
        private IsUse isUse;

        @NotNull(message = "{schedule.type.not-null}")
        private ScheduleType scheduleType;

        @NotNull(message = "{schedule.period.not-null}")
        private SchedulePeriod period;

        private Long customDay;

        @NotNull(message = "{common.list.not-null}")
        @UniqueElements(message = "{common.list.unique-elements}")
        private List<Long> todoList;

        public ScheduleServiceRequest.CreateSchedule toServiceRequest() {
            return ScheduleServiceRequest.CreateSchedule.builder()
                    .scheduleId(this.scheduleId)
                    .name(this.name)
                    .studyId(this.studyId)
                    .startDate(this.startDate)
                    .endDate(this.endDate)
                    .isUse(this.isUse)
                    .scheduleType(this.scheduleType)
                    .period(this.period)
                    .customDay(this.customDay)
                    .todoList(this.todoList)
                    .build();
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteSchedule {

        @NotNull(message = "{common.list.not-null}")
        @UniqueElements(message = "{common.list.unique-elements}")
        private List<Long> scheduleList;

        public ScheduleServiceRequest.DeleteSchedule toServiceRequest() {
            return ScheduleServiceRequest.DeleteSchedule.builder()
                    .scheduleList(this.scheduleList)
                    .build();
        }
    }
}
