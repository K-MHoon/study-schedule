package com.example.service.controller.request.study;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyMyControllerRequest {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class UpdateStudy {

        @NotEmpty
        private String studyName;

        @NotEmpty
        private String content;

        @NotNull
        private Long fullCount;

    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class UpdateStudyState {

        @NotEmpty
        private String state;
    }
}
