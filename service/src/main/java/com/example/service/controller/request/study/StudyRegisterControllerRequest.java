package com.example.service.controller.request.study;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyRegisterControllerRequest {

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class CreateStudyRegister {

        @NotEmpty
        private String goal;

        @NotEmpty
        private String objective;

        private String comment;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CancelStudyRegister {

        @UniqueElements
        @NotNull
        private List<Long> studyRegisterList;
    }
}
