package com.example.service.controller.request.schedule;

import com.example.service.service.schedule.request.TodoServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TodoControllerRequest {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class CreateTodo {

        @NotBlank
        private String title;

        @NotBlank
        private String content;

        public TodoServiceRequest.CreateTodo toServiceRequest() {
            return TodoServiceRequest.CreateTodo.builder()
                    .title(this.title)
                    .content(this.content)
                    .build();
        }
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteTodo {

        @UniqueElements
        @NotNull
        private List<Long> todoList;

        public TodoServiceRequest.DeleteTodo toServiceRequest() {
            return TodoServiceRequest.DeleteTodo.builder()
                    .todoList(this.todoList)
                    .build();
        }
    }
}
