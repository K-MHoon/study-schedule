package com.example.service.service.study.request;

import com.example.common.enums.IsUse;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StudyServiceRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class CreateStudy {

        private String studyName;
        private String content;
        private Boolean secret;
        private String password;
        private Long fullCount;
        private IsUse isUse;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString
    public static class ChangeSecret {

        private Boolean secret;
        private String password;
    }
}
