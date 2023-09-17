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

        @NotBlank(message = "{todo.id.not-blank}")
        private String title;

        @NotBlank(message = "{todo.content.not-blank}")
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

        @NotNull(message = "{common.list.not-null}")
        @UniqueElements(message = "{common.list.unique-elements}")
        private List<Long> todoList;

        public TodoServiceRequest.DeleteTodo toServiceRequest() {
            return TodoServiceRequest.DeleteTodo.builder()
                    .todoList(this.todoList)
                    .build();
        }
    }
}
