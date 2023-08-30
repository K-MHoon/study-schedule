package com.example.service.service.study.request;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyRegisterServiceRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class CreateStudyRegister {

        private String goal;
        private String objective;
        private String comment;
    }
}
