package com.example.studyschedule.model.request.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoControllerRequest {

    @Getter
    @AllArgsConstructor
    @ToString
    public static class CreateTodoRequest {

        @NotBlank
        private String title;

        @NotBlank
        private String content;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteTodoRequest {

        @UniqueElements
        @NotNull
        private List<Long> todoList;
    }
}
