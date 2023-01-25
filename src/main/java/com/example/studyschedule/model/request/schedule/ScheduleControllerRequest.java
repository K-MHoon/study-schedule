package com.example.studyschedule.model.request.schedule;

import com.example.studyschedule.enums.IsUse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ScheduleControllerRequest {

    @Getter
    @AllArgsConstructor
    @ToString
    public static class CreateScheduleRequest {

        @NotBlank
        private String name;

        @NotNull
        private LocalDateTime startDate;

        @NotNull
        private LocalDateTime endDate;

        @NotNull
        private IsUse isUse;

        @NotNull
        @UniqueElements
        private List<Long> todoList;
    }
}
