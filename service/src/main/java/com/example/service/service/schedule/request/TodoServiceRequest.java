package com.example.service.service.schedule.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoServiceRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class CreateTodo {

        private String title;
        private String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class DeleteTodo {

        private List<Long> todoList;
    }

}
