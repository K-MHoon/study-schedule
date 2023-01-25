package com.example.studyschedule.model.request.schedule;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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
}
