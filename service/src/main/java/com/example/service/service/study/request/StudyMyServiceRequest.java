package com.example.service.service.study.request;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyMyServiceRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static final class UpdateStudy {

        private String studyName;
        private String content;
        private Long fullCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static final class UpdateStudyState {

        private String state;
    }
}
