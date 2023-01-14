package com.example.studyschedule.model.request.schedule;

import com.example.studyschedule.enums.IsUse;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleControllerRequest {

    @Getter
    @AllArgsConstructor
    @ToString
    public static class CreateScheduleRequest {

        @NotNull
        private LocalDateTime startDate;

        @NotNull
        private LocalDateTime endDate;

        @NotNull
        private IsUse isUse;

        @NotNull
        private List<Long> todoList;
    }
}
